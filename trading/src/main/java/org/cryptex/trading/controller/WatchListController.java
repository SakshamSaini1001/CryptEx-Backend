package com.crypto.trading.controller;

import com.crypto.trading.model.Coin;
import com.crypto.trading.model.User;
import com.crypto.trading.model.WatchList;
import com.crypto.trading.service.CoinService;
import com.crypto.trading.service.UserService;
import com.crypto.trading.service.WatchListService;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
public class WatchListController {

    @Autowired
    private WatchListService watchListService;
    @Autowired
    private UserService userService;
    @Autowired
    private CoinService coinService;

    @GetMapping("/user")
    public ResponseEntity<WatchList> getUserWatchList(@RequestHeader("Authorization")String jwt) throws Exception {
        User user = userService.findUserbyJwt(jwt);
        WatchList watchList = watchListService.findUserWatchList(user.getId());
        return ResponseEntity.ok(watchList);
    }

    @GetMapping("/{watchlistid}")
    public ResponseEntity<WatchList> getWatchList(@PathVariable("watchlistid") Long watchlistid) throws Exception {
        WatchList watchList = watchListService.findById(watchlistid);
        return ResponseEntity.ok(watchList);
    }

    @PatchMapping("/add/coin/{coinId}")
    public ResponseEntity<Coin> addCoin(@RequestHeader("Authorization")String jwt,@PathVariable("coinId") String coinId) throws Exception {
        User user = userService.findUserbyJwt(jwt);
        Coin coin = coinService.findById(coinId);
        Coin addedCoin=watchListService.addItemsToWatchList(coin,user);
        return ResponseEntity.ok(addedCoin);
    }
}
