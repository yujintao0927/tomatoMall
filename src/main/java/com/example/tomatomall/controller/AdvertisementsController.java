package com.example.tomatomall.controller;

import com.example.tomatomall.po.Advertisements;
import com.example.tomatomall.service.AdvertisementsService;
import com.example.tomatomall.vo.AdvertisementsVO;
import com.example.tomatomall.vo.Response;
import lombok.Getter;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/advertisements")
public class AdvertisementsController {


    @Resource
    private AdvertisementsService advertisementsService;

    @GetMapping
    public Response<List<AdvertisementsVO>> getAdvertisements() {
        return Response.buildSuccess(advertisementsService.getAllAdvertisements());
    }

    @PutMapping
    public Response<String> updateAdvertisements(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "content", required = false) String content,
            @RequestParam(name = "imgUrl", required = false) String imgUrl,
            @RequestParam(name = "productId") Integer productId) {
        advertisementsService.updateAdvertisements(id, title, content, imgUrl, productId);
        return Response.buildSuccess("更新成功");
    }

    @PostMapping
    public Response<AdvertisementsVO> createAdvertisements(@RequestBody AdvertisementsVO advertisementsVO) {
        return Response.buildSuccess(advertisementsService.createAdvertisements(advertisementsVO));
    }

    @DeleteMapping("/{id}")
    public Response<String> deleteAdvertisements(@PathVariable Integer id){
        advertisementsService.deleteAdvertisements(id);
        return Response.buildSuccess("删除成功");
    }



}
