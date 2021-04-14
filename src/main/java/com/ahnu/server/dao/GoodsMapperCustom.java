package com.ahnu.server.dao;

import com.ahnu.server.model.Goods;
import com.ahnu.server.model.GoodsWrap;
import com.ahnu.server.model.Member;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface GoodsMapperCustom extends GoodsMapper{
    List<GoodsWrap> selectAll();
    List<GoodsWrap> getByState(int uid,Byte state);
    List<GoodsWrap> selectByType(int catelogId);
    List<GoodsWrap> searchByEqual(Goods goods);
    GoodsWrap getById(int id);
    List<HashMap<String, String>> getCateLogs();
    List<Goods> getLimit(int limit);
}
