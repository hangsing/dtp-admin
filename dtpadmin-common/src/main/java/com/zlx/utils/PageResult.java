package com.zlx.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class PageResult<T> {

    public final List<T> content;

    public final long totalElements;
}
