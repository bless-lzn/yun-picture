package com.yupi.lipicture.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.lipicture.application.service.PictureApplicationService;
import com.yupi.lipicture.domain.picture.entity.Picture;
import com.yupi.lipicture.domain.picture.service.PictureDomainService;
import com.yupi.lipicture.domain.user.entity.User;
import com.yupi.lipicture.infrastructure.api.aliyunai.model.CreateOutPaintingTaskResponse;
import com.yupi.lipicture.infrastructure.mapper.PictureMapper;
import com.yupi.lipicture.interfaces.dto.picture.*;
import com.yupi.lipicture.interfaces.vo.picturevo.PictureVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author henan
 * @description 针对表【picture(图片)】的数据库操作Service实现
 * @createDate 2025-07-03 20:52:04
 */
@Service
@Slf4j
public class PictureApplicationServiceImpl extends ServiceImpl<PictureMapper, Picture>
        implements PictureApplicationService {

    @Resource
    private PictureDomainService pictureDomainService;

    @Override
    public PictureVO uploadPicture(PictureUploadRequest pictureUploadRequest, Object inputSource, User loginUser) {
     return pictureDomainService.uploadPicture( pictureUploadRequest,  inputSource, loginUser);

    }

    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {

        return pictureDomainService.getQueryWrapper(pictureQueryRequest);
    }

    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {

        return pictureDomainService.getPictureVO( picture,  request);
    }

    @Override
    @Async
    public void cleanPictureFile(Picture picture) {
        pictureDomainService.cleanPictureFile(picture);

    }

//    @Override
//    @Deprecated
//    public void checkPictureAuth(User loginUser, Picture picture) {
//        Long spaceId = picture.getSpaceId();
//        if (spaceId == null) {
//            // 公共图库，仅本人或管理员可操作
//            if (!picture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
//                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//            }
//        } else {
//            // 私有空间，仅空间管理员可操作
//            if (!picture.getUserId().equals(loginUser.getId())) {
//                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//            }
//        }
//    }

    @Override
    public void deletePicture(long pictureId, User loginUser) {
       pictureDomainService.deletePicture(pictureId, loginUser);
    }

    @Override
    public List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser) {
       return pictureDomainService.searchPictureByColor(spaceId, picColor, loginUser);

    }


    /**
     * 分页获取图片封装
     */
    @Override
    public Page<PictureVO> getPictureVOPage(PictureQueryRequest pictureQueryRequest, HttpServletRequest request) {


       return pictureDomainService.getPictureVOPage(pictureQueryRequest, request);
    }

    @Override
    public Page<PictureVO> switchPictureVOPage(Page<Picture> picturePage) {
      return pictureDomainService.switchPictureVOPage(picturePage);
    }

    @Override
    public void validPicture(Picture picture) {
        pictureDomainService.validPicture(picture);
    }


    /**
     * 编辑（图片）
     *
     * @param pictureEditRequest
     * @param loginUser
     */
    @Override
    public void editPicture(PictureEditRequest pictureEditRequest, User loginUser) {
      pictureDomainService.editPicture(pictureEditRequest, loginUser);
    }

    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        pictureDomainService.doPictureReview(pictureReviewRequest, loginUser);

    }


    @Override
    public Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {

      return pictureDomainService.uploadPictureByBatch(pictureUploadByBatchRequest, loginUser);
    }

    @Override
    public void fillReviewParams(Picture picture, User loginUser) {
       pictureDomainService.fillReviewParams(picture, loginUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    //批量编辑图片
    public void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser) {
       pictureDomainService.editPictureByBatch(pictureEditByBatchRequest, loginUser);


    }

    @Override
    public CreateOutPaintingTaskResponse createOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser) {
      return pictureDomainService.createOutPaintingTask(createPictureOutPaintingTaskRequest, loginUser);
    }




}

