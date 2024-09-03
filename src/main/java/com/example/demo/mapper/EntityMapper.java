package com.example.demo.mapper;

import java.util.List;

public interface EntityMapper<T, R, E> {
    public R toBasicResponse(T entity);

    public List<R> toBasicResponse(List<T> entities);

    public T toModel(E dto);
}
