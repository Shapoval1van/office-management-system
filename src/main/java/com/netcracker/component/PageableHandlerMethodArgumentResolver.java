package com.netcracker.component;


import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.common.impl.SimplePageable;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public final class PageableHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().equals(Pageable.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

        int pageNumber = 0;
        int pageSize = 0;
        String sort = "";
        try {
            pageNumber = Integer.parseInt(nativeWebRequest.getParameter("page"));
            pageSize = Integer.parseInt(nativeWebRequest.getParameter("size"));
            sort = nativeWebRequest.getParameter("sort");
        } catch (Exception e){
            pageNumber = pageNumber==0?SimplePageable.DEFAULT_PAGE_NUMBER:pageNumber;
            pageSize = pageSize==0?SimplePageable.DEFAULT_PAGE_SIZE:pageSize;
        }

        return new SimplePageable(pageSize, pageNumber, sort);
    }
}
