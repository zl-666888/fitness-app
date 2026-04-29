package com.fitness.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fitness.common.PageResult;
import com.fitness.common.Result;
import com.fitness.dto.request.FoodRequest;
import com.fitness.entity.Food;
import com.fitness.interceptor.AdminAuthInterceptor;
import com.fitness.service.AdminService;
import com.fitness.service.FoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "管理端-食物管理", description = "食物CRUD、批量导入")
@RestController
@RequestMapping("/api/admin/foods")
public class AdminFoodController {

    private final FoodService foodService;
    private final AdminService adminService;

    public AdminFoodController(FoodService foodService, AdminService adminService) {
        this.foodService = foodService;
        this.adminService = adminService;
    }

    @Operation(summary = "食物列表（管理端）", description = "查看所有食物，包括下架的")
    @GetMapping
    public Result<PageResult<Food>> getFoodList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category) {
        IPage<Food> result = foodService.getFoodList(page, size, category);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "添加食物")
    @PostMapping
    public Result<Food> createFood(@Valid @RequestBody FoodRequest request,
                                    HttpServletRequest httpRequest) {
        Food food = foodService.createFood(request);
        Integer adminId = AdminAuthInterceptor.getCurrentAdminId();
        adminService.addLog(adminId, "add_food", "food", food.getId(),
                "添加食物", httpRequest.getRemoteAddr());
        return Result.success(food);
    }

    @Operation(summary = "编辑食物")
    @PutMapping("/{id}")
    public Result<Food> updateFood(@PathVariable Integer id,
                                    @Valid @RequestBody FoodRequest request,
                                    HttpServletRequest httpRequest) {
        Food food = foodService.updateFood(id, request);
        Integer adminId = AdminAuthInterceptor.getCurrentAdminId();
        adminService.addLog(adminId, "update_food", "food", id,
                "编辑食物", httpRequest.getRemoteAddr());
        return Result.success(food);
    }

    @Operation(summary = "删除食物")
    @DeleteMapping("/{id}")
    public Result<Void> deleteFood(@PathVariable Integer id, HttpServletRequest httpRequest) {
        foodService.deleteFood(id);
        Integer adminId = AdminAuthInterceptor.getCurrentAdminId();
        adminService.addLog(adminId, "delete_food", "food", id,
                "删除食物", httpRequest.getRemoteAddr());
        return Result.success();
    }

    @Operation(summary = "批量导入食物", description = "批量导入食物数据")
    @PostMapping("/import")
    public Result<Void> batchImport(@RequestBody List<FoodRequest> foods,
                                     HttpServletRequest httpRequest) {
        foodService.batchImport(foods);
        Integer adminId = AdminAuthInterceptor.getCurrentAdminId();
        adminService.addLog(adminId, "import_foods", "food", null,
                "批量导入食物数据", httpRequest.getRemoteAddr());
        return Result.success();
    }
}
