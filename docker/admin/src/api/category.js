import request from "./request";

// 分页查询分类
export function getCategoryPage(params) {
  return request({
    url: "/admin/category/page",
    method: "get",
    params,
  });
}

// 新增分类
export function addCategory(data) {
  return request({
    url: "/admin/category/add",
    method: "post",
    data,
  });
}

// 更新分类
export function updateCategory(data) {
  return request({
    url: "/admin/category/update",
    method: "post",
    data,
  });
}

// 删除分类
export function deleteCategory(id) {
  return request({
    url: `/admin/category/delete/${id}`,
    method: "delete",
  });
}

// 启用 / 禁用
export function toggleCategory(id) {
  return request({
    url: `/admin/category/toggle/${id}`,
    method: "post",
  });
}

// 批量排序
export function sortCategory(data) {
  return request({
    url: "/admin/category/sort",
    method: "post",
    data,
  });
}
