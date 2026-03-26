package com.baymax.exam.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baymax.exam.common.core.result.Result;
import com.baymax.exam.user.model.UserAuthInfo;
import com.baymax.exam.user.service.impl.UserAuthInfoServiceImpl;
import com.baymax.exam.web.annotation.Inner;
import com.baymax.exam.web.utils.UserAuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * VIEW 前端控制器
 * </p>
 *
 * @author baymax
 * @since 2022-12-14
 */
@RestController
@RequestMapping("/user-auth")
public class UserAuthInfoController {
    @Autowired
    UserAuthInfoServiceImpl userAuthInfoService;
    @Inner
    @Operation(summary = "学生认证信息")
    @GetMapping("/info/{userId}")
    public UserAuthInfo getAuthInfo(@PathVariable Integer userId){
        return userAuthInfoService.getStudentByUserId(userId);
    }
    @Operation(summary = "学生认证信息")
    @GetMapping("/info")
    public Result getAuthInfo(){
        Integer userId = UserAuthUtil.getUserId();
        return Result.success(userAuthInfoService.getStudentByUserId(userId));
    }

    @Inner
    @Operation(summary = "学生列表")
    @GetMapping("/student/list")
    public Result<List<UserAuthInfo>> getStudentList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize){
        LambdaQueryWrapper<UserAuthInfo> queryWrapper = new LambdaQueryWrapper<>();
        if(keyword != null && !keyword.isEmpty()){
            queryWrapper.like(UserAuthInfo::getRealName, keyword)
                    .or()
                    .like(UserAuthInfo::getJobNo, keyword);
        }
        queryWrapper.orderByDesc(UserAuthInfo::getStudentId);
        List<UserAuthInfo> list = userAuthInfoService.list(queryWrapper);
        return Result.success(list);
    }
}
