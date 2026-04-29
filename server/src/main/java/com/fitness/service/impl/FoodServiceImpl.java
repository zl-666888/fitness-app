package com.fitness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.common.exception.BizException;
import com.fitness.dto.request.FoodCalculateRequest;
import com.fitness.dto.request.FoodRequest;
import com.fitness.dto.response.FoodCalculateResponse;
import com.fitness.entity.Food;
import com.fitness.mapper.FoodMapper;
import com.fitness.service.FoodService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class FoodServiceImpl implements FoodService {

    private final FoodMapper foodMapper;

    public FoodServiceImpl(FoodMapper foodMapper) {
        this.foodMapper = foodMapper;
    }

    @Override
    public IPage<Food> getFoodList(Integer page, Integer size, String category) {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<Food>()
                .eq(Food::getStatus, 1);
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Food::getCategory, category);
        }
        wrapper.orderByDesc(Food::getCreatedAt);
        return foodMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public Food getFoodDetail(Integer foodId) {
        Food food = foodMapper.selectById(foodId);
        if (food == null) {
            throw BizException.notFound("食物不存在");
        }
        return food;
    }

    @Override
    public IPage<Food> searchFoods(Integer page, Integer size, String keyword) {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<Food>()
                .eq(Food::getStatus, 1);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Food::getName, keyword);
        }
        wrapper.orderByDesc(Food::getCreatedAt);
        return foodMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public FoodCalculateResponse calculate(FoodCalculateRequest request) {
        Food food = foodMapper.selectById(request.getFoodId());
        if (food == null) {
            throw BizException.notFound("食物不存在");
        }

        BigDecimal ratio = request.getAmount().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        FoodCalculateResponse resp = new FoodCalculateResponse();
        resp.setFoodName(food.getName());
        resp.setAmount(request.getAmount());

        if (food.getCalories() != null) {
            resp.setCalories(food.getCalories().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
        }
        if (food.getProtein() != null) {
            resp.setProtein(food.getProtein().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
        }
        if (food.getFat() != null) {
            resp.setFat(food.getFat().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
        }
        if (food.getCarbohydrate() != null) {
            resp.setCarbohydrate(food.getCarbohydrate().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
        }
        return resp;
    }

    @Override
    public Food createFood(FoodRequest request) {
        Food food = new Food();
        food.setName(request.getName());
        food.setCategory(request.getCategory());
        food.setCalories(request.getCalories());
        food.setProtein(request.getProtein());
        food.setFat(request.getFat());
        food.setCarbohydrate(request.getCarbohydrate());
        food.setFiber(request.getFiber());
        food.setUnit(request.getUnit());
        food.setImageUrl(request.getImageUrl());
        foodMapper.insert(food);
        return food;
    }

    @Override
    public Food updateFood(Integer foodId, FoodRequest request) {
        Food food = foodMapper.selectById(foodId);
        if (food == null) {
            throw BizException.notFound("食物不存在");
        }
        food.setName(request.getName());
        food.setCategory(request.getCategory());
        food.setCalories(request.getCalories());
        food.setProtein(request.getProtein());
        food.setFat(request.getFat());
        food.setCarbohydrate(request.getCarbohydrate());
        food.setFiber(request.getFiber());
        food.setUnit(request.getUnit());
        food.setImageUrl(request.getImageUrl());
        foodMapper.updateById(food);
        return food;
    }

    @Override
    public void deleteFood(Integer foodId) {
        if (foodMapper.selectById(foodId) == null) {
            throw BizException.notFound("食物不存在");
        }
        foodMapper.deleteById(foodId);
    }

    @Override
    public void batchImport(List<FoodRequest> foods) {
        for (FoodRequest req : foods) {
            Food food = new Food();
            food.setName(req.getName());
            food.setCategory(req.getCategory());
            food.setCalories(req.getCalories());
            food.setProtein(req.getProtein());
            food.setFat(req.getFat());
            food.setCarbohydrate(req.getCarbohydrate());
            food.setFiber(req.getFiber());
            food.setUnit(req.getUnit());
            foodMapper.insert(food);
        }
    }

    @Override
    public long countTotalFoods() {
        return foodMapper.selectCount(null);
    }
}
