package com.ahnu.server.controller;

import com.ahnu.server.dao.ChatSessionMapperCustom;
import com.ahnu.server.dao.CommentMapper;
import com.ahnu.server.dao.GoodsMapper;
import com.ahnu.server.dao.GoodsMapperCustom;
import com.ahnu.server.helper.FastDFSHelper;
import com.ahnu.server.model.Comment;
import com.ahnu.server.model.Goods;
import com.ahnu.server.model.ResJsonBody;
import com.ahnu.server.serivce.impl.GoodsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.websocket.server.PathParam;
import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    GoodsServiceImpl service;
    FastDFSHelper fastDfsHelper;
    ChatSessionMapperCustom chatSessionMapperCustom;
    CommentMapper commentMapper;
    static Logger logger;

    GoodsMapper goodsMapper;
    GoodsMapperCustom goodsMapperCustom;

    static {
        logger = LoggerFactory.getLogger(GoodsController.class);
    }

    @Autowired
    public GoodsController(GoodsServiceImpl service, FastDFSHelper fastDfsHelper, ChatSessionMapperCustom chatSessionMapperCustom, CommentMapper commentMapper, GoodsMapper goodsMapper, GoodsMapperCustom goodsMapperCustom) {
        this.service = service;
        this.fastDfsHelper = fastDfsHelper;
        this.chatSessionMapperCustom = chatSessionMapperCustom;
        this.commentMapper = commentMapper;
        this.goodsMapper = goodsMapper;
        this.goodsMapperCustom = goodsMapperCustom;
    }

    @PostMapping("/search")
    public ResJsonBody search(@RequestParam("pageSize") Integer pageSize,
                              @RequestParam("pageNum") Integer pageNum,
                              @RequestBody Goods goods) throws Exception {
        ResJsonBody response = new ResJsonBody();
        response.setContent(service.searchByEqual(pageSize, pageNum, goods));
        response.setCode(200);
        return response;
    }

    @PostMapping("/deal/{gid}")
    public ResJsonBody dealGood(@PathVariable("gid")int gid)throws Exception{
        ResJsonBody response = new ResJsonBody();
        Goods sel = goodsMapper.selectByPrimaryKey(gid);
        int uid = (int) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        if(uid == sel.getUid() && sel.getState()==0){
            sel.setState((byte) 1);
            goodsMapper.updateByPrimaryKey(sel);
        }else{
            response.setCode(6001);
        }
        return response;
    }
    @GetMapping("")
    public ResJsonBody getByCateLogId(@RequestParam("pageSize") Integer pageSize, @RequestParam("pageNum") Integer pageNum,
                                      @RequestParam(value = "catelogId", required = false) Integer catelogId) throws Exception {
        ResJsonBody response = new ResJsonBody();
        if (catelogId != null) {
            response.setContent(service.getAllByTypeDefault(pageSize, pageNum, catelogId));
        } else {
            response.setContent(service.getAllByPageDefault(pageSize, pageNum));
        }
        response.setCode(200);
        return response;
    }

    @GetMapping("/pre/{gid}")
    public ResJsonBody getById(@PathVariable("gid")Integer gid) {
        ResJsonBody jsonBody = new ResJsonBody();
        jsonBody.setContent(goodsMapper.selectByPrimaryKey(gid));
        return jsonBody;
    }

    private static class PostContent {
        private String title;
        private String[] imgs;
        private String content;
        private BigDecimal price;
        private int cateLogId;

        public int getCateLogId() {
            return cateLogId;
        }

        public void setCateLogId(int cateLogId) {
            this.cateLogId = cateLogId;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String[] getImgs() {
            return imgs;
        }

        public void setImgs(String[] imgs) {
            this.imgs = imgs;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    @GetMapping("/{id}")
    public ResJsonBody getById(@PathVariable("id") int id) throws Exception {
        ResJsonBody response = new ResJsonBody();
        response.setContent(service.getGoodsDetail(id));
        return response;
    }

    @GetMapping("/catelog")
    public ResJsonBody getCateLog() throws Exception {
        ResJsonBody jsonBody = new ResJsonBody();
        jsonBody.setContent(goodsMapperCustom.getCateLogs());
        return jsonBody;
    }

    @PreAuthorize("hasRole('user')")
    @GetMapping("/{id}/session")
    public ResJsonBody getSessionId(@PathVariable("id") int gid) {
        ResJsonBody jsonBody = new ResJsonBody();
        int uid = (int) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        jsonBody.setContent(chatSessionMapperCustom.getSessionIdByUid(uid, gid));
        return jsonBody;
    }

    @GetMapping("/hot")
    public ResJsonBody getHotGoods() throws Exception {
        ResJsonBody resJsonBody = new ResJsonBody();
        resJsonBody.setContent(service.getHot(10));
        return resJsonBody;
    }

    @PreAuthorize("hasRole('user')")
    @PostMapping("/comment")
    public ResJsonBody addComment(@RequestParam(required = true)String content,@RequestParam(required = true) Integer gid){
        ResJsonBody response = new ResJsonBody();
        Comment comment = new Comment();
        comment.setgId(gid);
        comment.setrTime(new Date());
        comment.setuId((Integer) SecurityContextHolder.getContext().getAuthentication().getCredentials());
        comment.setContent(content);
        commentMapper.insert(comment);
        return response;
    }

    @GetMapping("/comment/{gid}")
    public ResJsonBody getComment(@PathVariable("gid")Integer gid){
        ResJsonBody response = new ResJsonBody();
        response.setContent(commentMapper.selectByGid(gid));
        return response;
    }

    @PreAuthorize("hasRole('user')")
    @PostMapping("/upload")
    public ResJsonBody upload(@RequestParam("file") MultipartFile file) {
        ResJsonBody response = new ResJsonBody();
        if (file.isEmpty()) {
            response.setContent("fail");
        }
        String fileName = file.getOriginalFilename();
        final String filePath = "/Users/mano233/Documents/GradProject/server/tmp/";
        File dest = new File(filePath + fileName);
        try {
            file.transferTo(dest);
            String[] ret = fastDfsHelper.uploadFile(filePath + fileName);
            response.setContent(ret);
            response.setMsg("ok");
        } catch (Exception e) {
            logger.error(e.toString());
        } finally {
            dest.delete();
        }
        return response;
    }

    static class ContentWraper {
        private String text;
        private String[] imgs;

        public ContentWraper(String text, String[] imgs) {
            this.text = text;
            this.imgs = imgs;
        }

        public String getText() {
            return text;
        }

        public String[] getImgs() {
            return imgs;
        }
    }

    @PostMapping("/state/{uid}/{state}")
    public ResJsonBody getGoodsByState(@PathVariable("uid")int uid,@PathVariable("state") int state){
        ResJsonBody resJsonBody = new ResJsonBody();
        // int uid = (Integer) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        resJsonBody.setContent(goodsMapperCustom.getByState(uid,(byte)state));
        return resJsonBody;
    }
    @PostMapping("/delete/{gid}")
    public ResJsonBody deleteGood(@PathVariable("gid")int gid)throws Exception{
        ResJsonBody resJsonBody = new ResJsonBody();
        int contextUid = (Integer) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        boolean isAdmin = false;
        for(GrantedAuthority s: SecurityContextHolder.getContext().getAuthentication().getAuthorities()){
            if(s.getAuthority().contains("admin")){
                isAdmin = true;
                break;
            }
        }
        Goods selGood = goodsMapper.selectByPrimaryKey(gid);
        if(selGood==null){
            resJsonBody.setMsg("找不到商品");
            return resJsonBody;
        }
        Integer createUser = selGood.getUid();
        if(isAdmin){
            resJsonBody.setMsg("删除成功（由管理员操作）");
            goodsMapper.deleteByPrimaryKey(gid);
        }else{
            if(createUser != contextUid){
                resJsonBody.setCode(6001);
                resJsonBody.setMsg("你无权删除这个商品");
            }else{
                resJsonBody.setMsg("删除成功");
                // selGood.setState((byte) -1);
                // goodsMapper.updateByPrimaryKey(selGood);
                goodsMapper.deleteByPrimaryKey(gid);
            }
        }
        return resJsonBody;
    }

    @PostMapping("")
    public ResJsonBody createGood(@RequestBody PostContent postContent) throws Exception {
        ResJsonBody resJsonBody = new ResJsonBody();
        ObjectMapper mapper = new ObjectMapper();
        for (String src : postContent.imgs) {
            System.out.println(src);
        }
        Goods insert = new Goods();
        insert.setPrice(postContent.price);
        ContentWraper contentWraper = new ContentWraper(postContent.content, postContent.imgs);
        String content = mapper.writeValueAsString(contentWraper);
        insert.setContnet(content);
        insert.setTitle(postContent.title);
        insert.setPrice(postContent.price);
        insert.setCtime(new Date());
        insert.setPtime(insert.getCtime());
        insert.setState((byte) 0);
        insert.setCatelogId(postContent.getCateLogId());
        insert.setUid((Integer) SecurityContextHolder.getContext().getAuthentication().getCredentials());
        if (postContent.getImgs().length > 0) {
            insert.setPreImg(postContent.getImgs()[0]);
        }
        goodsMapper.insert(insert);
        resJsonBody.setContent(insert.getId());
        return resJsonBody;
    }
}
