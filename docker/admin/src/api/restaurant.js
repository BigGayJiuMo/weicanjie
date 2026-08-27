import request from "./request";

// 分页查询餐厅列表
export function getRestaurantPage(params) {
  return request({
    url: "/admin/restaurant/page",
    method: "get",
    params,
  });
}

// 新增餐厅
export function addRestaurant(data) {
  return request({
    url: "/admin/restaurant/add",
    method: "post",
    data,
  });
}

// 更新餐厅
export function updateRestaurant(data) {
  return request({
    url: "/admin/restaurant/update",
    method: "post",
    data,
  });
}

// 删除餐厅
export function deleteRestaurant(id) {
  return request({
    url: `/admin/restaurant/delete/${id}`,
    method: "delete",
  });
}

// 切换营业状态
export function toggleRestaurant(id) {
  return request({
    url: `/admin/restaurant/toggle/${id}`,
    method: "post",
  });
}

export function setManualBusinessStatus(id, manualBusinessStatus) {
  return request({
    url: `/admin/restaurant/manual-status/${id}`,
    method: "post",
    data: { manualBusinessStatus }
  });
}


// 获取餐厅图片
export function listImages(restaurantId) {
  return request({
    url: `/admin/restaurant/image/list/${restaurantId}`,
    method: "get",
  });
}

// 添加图片
export function addImage(data) {
  return request({
    url: `/admin/restaurant/image/add`,
    method: "post",
    data,
  });
}

// 删除图片
export function deleteImage(id) {
  return request({
    url: `/admin/restaurant/image/delete/${id}`,
    method: "delete",
  });
}

// 分类显示
export function getRestaurantCategoryList() {
  return request.get("/admin/restaurant-category/list");
}

// 增加分类
export function addRestaurantCategory(data) {
  return request.post("/admin/restaurant-category/add", data);
}

// 更新分类
export function updateRestaurantCategory(data) {
  return request.post("/admin/restaurant-category/update", data);
}

// 删除分类
export function deleteRestaurantCategory(id) {
  return request.delete(`/admin/restaurant-category/delete/${id}`);
}

// 批量更新餐厅分类排序
export function updateRestaurantCategorySort(list) {
  return request.post("/admin/restaurant-category/sort", list);
}