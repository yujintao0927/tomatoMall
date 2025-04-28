package com.example.tomatomall.po;


import com.example.tomatomall.vo.AdvertisementsVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "advertisements")public class Advertisements {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(nullable = false, length = 50, name = "title")
    private String title;

    @Column(nullable = false, length = 500, name = "content")
    private String content;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public AdvertisementsVO toVO(){
        AdvertisementsVO vo = new AdvertisementsVO();
        vo.setId(id);
        vo.setTitle(title);
        vo.setContent(content);
        vo.setProduct_id(product.getId());
        vo.setImage_url(imageUrl);
        return vo;
    }
}
