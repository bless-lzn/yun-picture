package com.yupi.lipicture.application.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.lipicture.domain.picture.entity.Picture;
import com.yupi.lipicture.domain.user.entity.User;
import com.yupi.lipicture.infrastructure.api.aliyunai.model.CreateOutPaintingTaskResponse;
import com.yupi.lipicture.interfaces.dto.picture.*;
import com.yupi.lipicture.interfaces.vo.picturevo.PictureVO;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author henan
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2025-07-03 20:52:04
 */
public interface PictureApplicationService extends IService<Picture> {

    /**
     * 上传图片
     *
     * @param pictureUploadRequest
     * @param inputSource
     * @param  loginUser
     * @return
     */
    PictureVO uploadPicture(PictureUploadRequest pictureUploadRequest, Object inputSource, User loginUser);

    /**
     * 获取查询条件
     *
     * @param pictureQueryRequest
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取图片封装类
     *
     * @param picture
     * @param request
     * @return
     */
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request);


    @Async
    void cleanPictureFile(Picture picture);

//    void checkPictureAuth(User loginUser, Picture picture);

    void deletePicture(long pictureId, User loginUser);

    List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser);

    /**
     * 分页获取图片封装类
     *
     * @param pictureQueryRequest
     * @param request
     * @return
     */

    public Page<PictureVO> getPictureVOPage(PictureQueryRequest pictureQueryRequest, HttpServletRequest request);

    Page<PictureVO> switchPictureVOPage(Page<Picture> picturePage);

    /**
     * 校验
     *
     * @param picture
     */
    public void validPicture(Picture picture);

    void editPicture(PictureEditRequest pictureEditRequest, User loginUser);

    /**
     * 图片审核
     *
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser);

    /**
     * 填充审核参数
     *
     * @param picture
     * @param loginUser
     */
    void fillReviewParams(Picture picture, User loginUser);


    //批量编辑图片
    void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);



    /**
     * 创建图片外画任务
     *
     * @param createPictureOutPaintingTaskRequest
     * @param loginUser
     */
    CreateOutPaintingTaskResponse createOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser);

}
