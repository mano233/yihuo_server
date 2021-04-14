package com.ahnu.server.serivce;


import com.ahnu.server.dao.GoodsMapperCustom;
import com.ahnu.server.model.Goods;
import com.ahnu.server.model.GoodsWrap;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

public interface GoodsService {
    GoodsWrap getGoodsDetail(int goodsId) throws Exception;
    void updateGoods(Goods record) throws Exception;
    PageInfo<GoodsWrap> getAllByPageDefault(int pageSize,int pageNum) throws Exception;
    PageInfo<GoodsWrap> getAllByTypeDefault(int pageSize, int pageNum, int type) throws Exception;
    PageInfo<GoodsWrap> searchByEqual(int pageSize,int pageNum,Goods goods) throws Exception;
    ArrayList<Goods> getAll(int pageNum, int pageSize) throws Exception;
    void addOne(Goods goods) throws Exception;
    List<Goods> getHot(int limit) throws Exception;
}
