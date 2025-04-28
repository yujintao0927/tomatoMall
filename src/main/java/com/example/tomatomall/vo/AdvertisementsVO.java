package com.example.tomatomall.vo;

import com.example.tomatomall.po.Advertisements;
import com.example.tomatomall.po.Product;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class AdvertisementsVO {
    private Integer id;
    private String title;
    private String content;
    private String image_url;
    private Integer product_id;

    public Advertisements toPO(Product product) {
        Advertisements advertisements = new Advertisements();
        advertisements.setId(id);
        advertisements.setTitle(title);
        advertisements.setContent(content);
        advertisements.setImageUrl(image_url);
        advertisements.setProduct(product);
        return advertisements;
    }
}
