package com.crypto.trading.service;

import com.crypto.trading.model.Coin;
import com.crypto.trading.model.User;
import com.crypto.trading.model.WatchList;
import com.crypto.trading.repository.WatchListRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class WatchListService {

    @Autowired
    private WatchListRepo watchListRepo;

    public WatchList findUserWatchList(Long userId) throws Exception {
        WatchList watchList = watchListRepo.findByUserId(userId);
        if(watchList == null) {
            throw new Exception("watchlist not found");
        }
        return watchList;
    }

    public WatchList createWatchList(User user) {
        WatchList watchList = new WatchList();
        watchList.setUser(user);

        return watchListRepo.save(watchList);
    }

    public WatchList findById(Long id) throws Exception {
        Optional<WatchList> optionalwatchList = watchListRepo.findById(id);
        if(optionalwatchList.isEmpty()) {
            throw new Exception("watchlist not found");
        }
        return optionalwatchList.get();
    }

    public Coin addItemsToWatchList(Coin coin, User user) throws Exception {
        WatchList watchList=findUserWatchList(user.getId());
        if (watchList.getCoins().contains(coin)) {
            watchList.getCoins().remove(coin);
        }
        else
            watchList.getCoins().add(coin);
        watchListRepo.save(watchList);
        return coin;
    }
}
