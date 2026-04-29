package com.fitness.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fitness.dto.request.FoodCalculateRequest;
import com.fitness.dto.request.FoodRequest;
import com.fitness.dto.response.FoodCalculateResponse;
import com.fitness.entity.Food;

import java.util.List;

public interface FoodService {
    IPage<Food> getFoodList(Integer page, Integer size, String category);
    Food getFoodDetail(Integer foodId);
    IPage<Food> searchFoods(Integer page, Integer size, String keyword);
    FoodCalculateResponse calculate(FoodCalculateRequest request);
    Food createFood(FoodRequest request);
    Food updateFood(Integer foodId, FoodRequest request);
    void deleteFood(Integer foodId);
    void batchImport(List<FoodRequest> foods);
    long countTotalFoods();
}
