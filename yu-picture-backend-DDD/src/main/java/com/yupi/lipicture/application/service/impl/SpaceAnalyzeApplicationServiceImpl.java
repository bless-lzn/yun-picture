package com.yupi.lipicture.application.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.lipicture.application.service.SpaceApplicationService;
import com.yupi.lipicture.domain.picture.repository.PictureRepository;
import com.yupi.lipicture.infrastructure.exception.BusinessException;
import com.yupi.lipicture.infrastructure.exception.ErrorCode;
import com.yupi.lipicture.infrastructure.exception.ThrowUtils;
import com.yupi.lipicture.infrastructure.manager.upload.model.dto.space.analyze.*;
import com.yupi.lipicture.interfaces.vo.sapce.analyze.*;
import com.yupi.lipicture.domain.picture.entity.Picture;
import com.yupi.lipicture.domain.space.entity.Space;
import com.yupi.lipicture.domain.user.entity.User;
import com.yupi.lipicture.application.service.SpaceAnalyzeApplicationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SpaceAnalyzeApplicationServiceImpl implements SpaceAnalyzeApplicationService {


    @Resource
    private SpaceApplicationService spaceApplicationService;

    @Resource
    private PictureRepository  pictureRepository;


    @Override
    public void checkSpaceAnalyzeAuth(SpaceAnalyzeRequest spaceAnalyzeRequest, User loginUser) {
        // 检查权限
        if (spaceAnalyzeRequest.isQueryAll() || spaceAnalyzeRequest.isQueryPublic()) {
            // 全空间分析或者公共图库权限校验：仅管理员可访问
            ThrowUtils.throwIf(!loginUser.isAdmin(), ErrorCode.NO_AUTH_ERROR, "无权访问公共图库");
        } else {
            // 私有空间权限校验
            Long spaceId = spaceAnalyzeRequest.getSpaceId();
            ThrowUtils.throwIf(spaceId == null || spaceId <= 0, ErrorCode.PARAMS_ERROR);
            Space space = spaceApplicationService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            spaceApplicationService.checkSpaceAuth(loginUser, space);
        }
    }

    //填充查询参数
    @Override
    public void fillAnalyzeQueryWrapper(SpaceAnalyzeRequest spaceAnalyzeRequest, QueryWrapper<Picture> queryWrapper) {
        if (spaceAnalyzeRequest.isQueryAll()) {
            return;
        }
        if (spaceAnalyzeRequest.isQueryPublic()) {
            queryWrapper.isNull("spaceId");
            return;
        }
        //查询自己的
        Long spaceId = spaceAnalyzeRequest.getSpaceId();
        if (spaceId != null) {
            queryWrapper.eq("spaceId", spaceId);
            return;
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }


    @Override
    public SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser) {
        //1。判断是否为空
        ThrowUtils.throwIf(spaceUsageAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        //2.判断权限
        //如果为查询全空间或者公共空间

        if (spaceUsageAnalyzeRequest.isQueryAll() || spaceUsageAnalyzeRequest.isQueryPublic()) {
            boolean admin = loginUser.isAdmin();
            ThrowUtils.throwIf(!admin, ErrorCode.NO_AUTH_ERROR);
            //统计公共图库的使用资源
            QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("picSize");
            if (!spaceUsageAnalyzeRequest.isQueryAll()) {
                //查询公共空间
                queryWrapper.eq("spaceId", null);
            }
            List<Picture> pictureList = pictureRepository.getBaseMapper().selectList(queryWrapper);
            long usedSize = pictureList.stream().mapToLong(Picture::getPicSize).sum();
            long usedCount = pictureList.size();
            //封装返回结果
            SpaceUsageAnalyzeResponse spaceUsageAnalyzeResponse = new SpaceUsageAnalyzeResponse();
            spaceUsageAnalyzeResponse.setMaxSize(null);
            spaceUsageAnalyzeResponse.setMaxCount(null);
            spaceUsageAnalyzeResponse.setUsedSize(usedSize);
            spaceUsageAnalyzeResponse.setSizeUsageRatio(null);
            spaceUsageAnalyzeResponse.setUsedCount(usedCount);
            return spaceUsageAnalyzeResponse;
        }

        //如果是私有空间
        else {


            //获取空间使用情况
            //校验权限
            Long spaceId = spaceUsageAnalyzeRequest.getSpaceId();
            ThrowUtils.throwIf(spaceId == null, ErrorCode.PARAMS_ERROR);
            Space space = spaceApplicationService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            spaceApplicationService.checkSpaceAuth(loginUser, space);
            QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("picSize");
            queryWrapper.eq("spaceId", spaceId);
            //封装返回结果
            SpaceUsageAnalyzeResponse spaceUsageAnalyzeResponse = new SpaceUsageAnalyzeResponse();
            spaceUsageAnalyzeResponse.setMaxSize(space.getMaxSize());
            spaceUsageAnalyzeResponse.setMaxCount(space.getMaxCount());
            spaceUsageAnalyzeResponse.setUsedSize(space.getTotalSize());
            double sizeUsageRatio = NumberUtil.round(space.getTotalSize() * 1.0 * 100 / space.getMaxSize(), 2).doubleValue();
            spaceUsageAnalyzeResponse.setSizeUsageRatio(sizeUsageRatio);
            spaceUsageAnalyzeResponse.setUsedCount(space.getTotalCount());
            double countUsageRatio = NumberUtil.round(space.getTotalCount() * 1.0 * 100 / space.getMaxCount(), 2).doubleValue();
            spaceUsageAnalyzeResponse.setCountUsageRatio(countUsageRatio);
            return spaceUsageAnalyzeResponse;

        }
        //3.计算使用情况


    }

    @Override
    public List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser) {
        //校验权限
        checkSpaceAnalyzeAuth(spaceCategoryAnalyzeRequest, loginUser);
        //构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyzeQueryWrapper(spaceCategoryAnalyzeRequest, queryWrapper);
        queryWrapper.select("category AS category", "count(*) AS count", "SUM(picSize) AS totalSize").groupBy("category");

        List<SpaceCategoryAnalyzeResponse> responseList = pictureRepository.getBaseMapper().selectMaps(queryWrapper)
                .stream().map(result -> new SpaceCategoryAnalyzeResponse(result.get("category") != null ? result.get("category").toString() : "未分类",
                        ((Number) result.get("count")).longValue(),
                        ((Number) result.get("totalSize")).longValue()))
                .collect(Collectors.toList());
//        ((Number)result.get("count")).longValue()
        //查询转换结果
        return responseList;


    }

    @Override
    public List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser) {
        //校验参数
        this.checkSpaceAnalyzeAuth(spaceTagAnalyzeRequest, loginUser);
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        //填充参数
        fillAnalyzeQueryWrapper(spaceTagAnalyzeRequest, queryWrapper);
        queryWrapper.select("tags");
        List<String> tagJsonList = pictureRepository.getBaseMapper().selectObjs(queryWrapper).stream().filter(ObjUtil::isNotNull).map(Object::toString)//转换为字符串
                .collect(Collectors.toList());
        //扁平化处理["java","php"],["python","php"]=>["java","php","python","php"]
//        List<String> tagList = tagJsonList.stream().flatMap(tagJson -> JSONUtil.arra(tagJson, String.class).stream()).collect(Collectors.toList());
        Map<String, Long> map = tagJsonList.stream().flatMap(tagJson -> JSONUtil.toList(tagJson, String.class).stream()).collect(Collectors.groupingBy(tag -> tag, Collectors.counting()));
        List<SpaceTagAnalyzeResponse> response = map.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                //设置数据
                .map(entry -> new SpaceTagAnalyzeResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return response;
    }

    @Override

    //空间图片的大小分析
    public List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser) {
        //校验参数
        this.checkSpaceAnalyzeAuth(spaceSizeAnalyzeRequest, loginUser);
        //查询权限
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        //填充参数
        this.fillAnalyzeQueryWrapper(spaceSizeAnalyzeRequest, queryWrapper);
        queryWrapper.select("picSize");
        List<Long> picSize = pictureRepository.getBaseMapper().selectObjs(queryWrapper).stream()
                .map(obj -> (Long) obj).collect(Collectors.toList());
        //定义分段范围，注意使用有序Map
        LinkedHashMap<String, Long> rangeSizes = new LinkedHashMap<>();
        //<100k B
        rangeSizes.put("<100KB", picSize.stream().filter(size -> size < 100 * 1024).count());
        rangeSizes.put("100kB-500KB", picSize.stream().filter(size -> size >= 100 * 1024 && size < 500 * 1024).count());
        rangeSizes.put("<500KB-1MB", picSize.stream().filter(size -> size > 500 * 1024 && size < 1024 * 1024).count());
        rangeSizes.put(">1MB", picSize.stream().filter(size -> size > 1024 * 1024).count());

        //放到SpaceSizeAnalyzeResponse列表里面
        List<SpaceSizeAnalyzeResponse> spaceSizeAnalyzeResponses = new ArrayList<>();
        for (Map.Entry<String, Long> entry : rangeSizes.entrySet()) {
            SpaceSizeAnalyzeResponse spaceSizeAnalyzeResponse = new SpaceSizeAnalyzeResponse(entry.getKey(), entry.getValue());
            spaceSizeAnalyzeResponses.add(spaceSizeAnalyzeResponse);
        }
        return spaceSizeAnalyzeResponses;
    }

    //根据用户上传日期进行排行
    @Override
    public List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, User loginUser) {
        //权限检测
        checkSpaceAnalyzeAuth(spaceUserAnalyzeRequest, loginUser);
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        //补充查询条件
        Long userId = spaceUserAnalyzeRequest.getUserId();
        queryWrapper.eq(ObjUtil.isNotNull(userId), "userId", userId);
        //填充
        this.fillAnalyzeQueryWrapper(spaceUserAnalyzeRequest, queryWrapper);
        //day,week,month 查询数量

        //分析维度
        String timeDimension = spaceUserAnalyzeRequest.getTimeDimension();
        switch (timeDimension) {
            case "day":
                queryWrapper.select("DATE_FORMAT(createTime,'%Y-%m-%d')AS period", "COUNT(*) AS count");
                break;
            case "week":
                queryWrapper.select("YEARWEEK(createTime)AS period", "COUNT(*) AS count");
                break;
            case "month":
                queryWrapper.select("DATE_FORMAT(createTime,'%Y-%m')AS period", "COUNT(*) AS count");
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "时间维度错误");
        }
        //进行分组和排序
        queryWrapper.groupBy("period").orderByAsc("period");

        //查询结果并且进行转换
        return pictureRepository.getBaseMapper().selectMaps(queryWrapper).stream().map(map -> {
            SpaceUserAnalyzeResponse spaceUserAnalyzeResponse = new SpaceUserAnalyzeResponse();
            spaceUserAnalyzeResponse.setPeriod(map.get("period").toString());
            spaceUserAnalyzeResponse.setCount(((Number) map.get("count")).longValue());
            return spaceUserAnalyzeResponse;
        }).collect(Collectors.toList());

    }

    //获取前十名的空间使用
    @Override
    public List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser) {
        //只有管理员可以查看
        if (!loginUser.isAdmin()) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "spaceName", "totalSize", "userId");
        queryWrapper.orderByDesc("totalSize");
        queryWrapper.last("limit " + spaceRankAnalyzeRequest.getTopN());
        return spaceApplicationService.list(queryWrapper);
    }


}
