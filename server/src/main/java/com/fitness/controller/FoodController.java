package com.fitness.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fitness.common.PageResult;
import com.fitness.common.Result;
import com.fitness.dto.request.FoodCalculateRequest;
import com.fitness.dto.response.FoodCalculateResponse;
import com.fitness.entity.Food;
import com.fitness.service.FoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户端-食物", description = "食物热量查询、搜索、计算")
@RestController
@RequestMapping("/api/foods")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @Operation(summary = "食物列表", description = "按分类分页浏览食物")
    @GetMapping
    public Result<PageResult<Food>> getFoodList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category) {
        IPage<Food> result = foodService.getFoodList(page, size, category);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "搜索食物", description = "按食物名称搜索")
    @GetMapping("/search")
    public Result<PageResult<Food>> searchFoods(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        IPage<Food> result = foodService.searchFoods(page, size, keyword);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "食物详情")
    @GetMapping("/{id}")
    public Result<Food> getFoodDetail(@PathVariable Integer id) {
        return Result.success(foodService.getFoodDetail(id));
    }

    @Operation(summary = "热量计算", description = "根据食物ID和份量(g)计算实际摄入营养")
    @PostMapping("/calculate")
    public Result<FoodCalculateResponse> calculate(@Valid @RequestBody FoodCalculateRequest request) {
        return Result.success(foodService.calculate(request));
    }
}
