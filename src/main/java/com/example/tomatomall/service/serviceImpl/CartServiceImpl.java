package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.Repository.CartRepository;
import com.example.tomatomall.Repository.OrdersRepository;
import com.example.tomatomall.Repository.ProductRepository;
import com.example.tomatomall.Repository.StockpileRepository;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.*;
import com.example.tomatomall.service.CartService;
import com.example.tomatomall.utils.SecurityUtil;
import com.example.tomatomall.vo.CartVO;
import com.example.tomatomall.vo.OrdersVO;
import com.example.tomatomall.vo.WholeCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private StockpileRepository stockpileRepository;

    public CartVO addToCart(Integer productId, Integer quantity){
        Product product = productRepository.findById(productId).orElseThrow(TomatoMallException::productNotFound);
        Account account = securityUtil.getCurrentUser();
        Cart cart = new Cart();
        if(product.getStockpile().getAmount() < quantity){
            throw TomatoMallException.insufficientStock();
        }

        cart.setProduct(product);
        cart.setQuantity(quantity);
        cart.setAccount(account);
        cartRepository.save(cart);

        return cart.toVO();
    }

    public void deleteFromCart(Integer cartItemId){
        cartRepository.findById(cartItemId).orElseThrow(TomatoMallException::cartItemNotFound);
        cartRepository.deleteById(cartItemId);
    }


    public void changeQuantity(Integer cartItemId, Integer quantity){
        Cart cart = cartRepository.findById(cartItemId).orElseThrow(TomatoMallException::cartItemNotFound);
        cart.setQuantity(quantity);
        cartRepository.save(cart);
    }

    public WholeCart getCartList(){
        Account account = securityUtil.getCurrentUser();
        List<Cart> cartsOfThisAccount = cartRepository.findByAccount(account);
        WholeCart wholeCart = new WholeCart();
        wholeCart.setCarts(cartsOfThisAccount);
        wholeCart.setTotalAmount();
        wholeCart.setTotal();
        return wholeCart;
    }

    public OrdersVO checkout(List<Integer> cartItemId, String shoppingAddress, String paymentMethod){
        if(cartItemId == null || cartItemId.isEmpty()){
            return null;
        }

        Account currentUser = securityUtil.getCurrentUser();

        BigDecimal totalAmount = new BigDecimal(0);

//        List<Cart> cartsOfThisAccount = new ArrayList<>();
        for(Integer itemId : cartItemId){
            Cart cart = cartRepository.findById(itemId).orElseThrow(TomatoMallException::cartItemNotFound);
//            cartsOfThisAccount.add(cart);
            Product item = productRepository.findById(cart.getProduct().getId()).orElseThrow(TomatoMallException::productNotFound);

            if(item == null){
                throw TomatoMallException.cartItemNotFound();
            }

            Stockpile stockpile = item.getStockpile();
            if(stockpile.getAmount() >= cart.getQuantity()){
                throw TomatoMallException.insufficientStock();
            }
            stockpile.setAmount(stockpile.getAmount() - cart.getQuantity());
            stockpile.setFrozen(stockpile.getFrozen() + cart.getQuantity());
            stockpileRepository.save(stockpile);
            totalAmount = totalAmount.add(item.getPrice().multiply(new BigDecimal(cart.getQuantity()))) ;
        }

        Orders orders = new Orders();
        orders.setTotalAmount(totalAmount);
        orders.setUser(currentUser);
        orders.setPaymentMethod(paymentMethod);
        orders.setStatus("PENDING");
        orders.setCreateTime(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
        ordersRepository.save(orders);

        return orders.toVO();
    }

}
