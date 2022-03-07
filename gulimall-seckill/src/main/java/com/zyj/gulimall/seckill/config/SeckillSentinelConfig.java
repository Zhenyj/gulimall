package com.zyj.gulimall.seckill.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.alibaba.fastjson.JSON;
import com.zyj.common.exception.BizCodeEnum;
import com.zyj.common.utils.R;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义sentinel异常返回信息
 *
 * @author lulx
 * @date 2022-03-06 14:17
 **/
@Configuration
public class SeckillSentinelConfig implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        response.setContentType("application/json;charset=utf-8");
        R r = null;
        if (e instanceof FlowException) {
            r = R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(), BizCodeEnum.TOO_MANY_REQUEST.getMsg());
        } else if (e instanceof DegradeException) {
            r = R.error(BizCodeEnum.DEGRADE.getCode(), BizCodeEnum.DEGRADE.getMsg());
        } else if (e instanceof ParamFlowException) {
            r = R.error(BizCodeEnum.PARAM_FLOW.getCode(), BizCodeEnum.PARAM_FLOW.getMsg());
        } else if (e instanceof SystemBlockException) {
            r = R.error(BizCodeEnum.SYSTEM_BLOCK.getCode(), BizCodeEnum.SYSTEM_BLOCK.getMsg());
        } else if (e instanceof AuthorityException) {
            r = R.error(BizCodeEnum.AUTHORITY.getCode(), BizCodeEnum.AUTHORITY.getMsg());
        }
        response.getWriter().write(JSON.toJSONString(r));
    }
}
