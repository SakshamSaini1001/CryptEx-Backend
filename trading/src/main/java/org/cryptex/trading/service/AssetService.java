package com.crypto.trading.service;

import com.crypto.trading.model.Asset;
import com.crypto.trading.model.Coin;
import com.crypto.trading.model.User;
import com.crypto.trading.repository.AssetRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetService {

    @Autowired
    private AssetRepo assetRepo;

    public Asset createAsset(User user, Coin coin, double quantity) {
        Asset asset = new Asset();
        asset.setUser(user);
        asset.setCoin(coin);
        asset.setQuantity(quantity);
        asset.setBuyPrice(coin.getCurrentPrice());
        return assetRepo.save(asset);
    }

    public Asset getAssetById(Long assetId) throws Exception {
        return assetRepo.findById(assetId).orElseThrow(()->new Exception(("asset not found")));
    }

    public Asset getAssetByUserIdAndId(Long userId, Long assetId) {
        return null;
    }

    public List<Asset> getUserAssets(Long userId) {
        return assetRepo.findByUserId(userId);
    }

    public Asset updateAsset(Long assetId,double quantity) throws Exception {
        Asset oldAsset = getAssetById(assetId);
        oldAsset.setQuantity(quantity+oldAsset.getQuantity());
        return assetRepo.save(oldAsset);
    }

    public Asset findAssetByUserIdAndCoinId(Long userId, String coinId) {
        return assetRepo.findByUserIdAndCoinId(userId,coinId);
    }

    public void deleteAsset(Long assetId) {
        assetRepo.deleteById(assetId);
    }
}
