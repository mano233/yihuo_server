package com.ahnu.server.serivce.impl;

import com.ahnu.server.dao.GoodsMapper;
import com.ahnu.server.dao.GoodsMapperCustom;
import com.ahnu.server.model.Goods;
import com.ahnu.server.model.GoodsWrap;
import com.ahnu.server.serivce.GoodsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    public GoodsServiceImpl(GoodsMapperCustom goodsMapperCustom, GoodsMapper goodsMapper) {
        this.goodsMapperCustom = goodsMapperCustom;
        this.goodsMapper = goodsMapper;
    }

    GoodsMapperCustom goodsMapperCustom;
    GoodsMapper goodsMapper;

    @Override
    public GoodsWrap getGoodsDetail(int goodsId) throws Exception {
        return goodsMapperCustom.getById(goodsId);
    }

    @Override
    public void updateGoods(Goods record) throws Exception {
        goodsMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public PageInfo<GoodsWrap> getAllByPageDefault(int pageSize, int pageNum) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        PageHelper.orderBy("ptime desc");
        return new PageInfo<GoodsWrap>(goodsMapperCustom.selectAll());
    }

    @Override
    public PageInfo<GoodsWrap> getAllByTypeDefault(int pageSize, int pageNum, int type) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        PageHelper.orderBy("ptime desc");
        return new PageInfo<GoodsWrap>(goodsMapperCustom.selectByType(type));
    }

    @Override
    public PageInfo<GoodsWrap> searchByEqual(int pageSize, int pageNum, Goods goods) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        PageHelper.orderBy("ptime desc");
        return new PageInfo<GoodsWrap>(goodsMapperCustom.searchByEqual(goods));
    }

    @Override
    public ArrayList<Goods> getAll(int pageNum, int pageSize) throws Exception{
        var goods = new ArrayList<Goods>();
        goods.add(goodsMapper.selectByPrimaryKey(1));
        return goods;
    }

    @Override
    public void addOne(Goods goods) throws Exception {
       goodsMapper.insert(goods);
    }

    @Override
    public List<Goods> getHot(int limit) throws Exception {
        return goodsMapperCustom.getLimit(limit);
    }
}
