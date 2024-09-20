package com.crypto.trading.repository;

import com.crypto.trading.model.WatchList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchListRepo extends JpaRepository<WatchList, Long> {
    WatchList findByUserId(Long userId);
}
