import request from "./request";

export function getDishPage(params) {
  return request({
    url: "/admin/dish/page",
    method: "get",
    params,
  });
}

export function addDish(data) {
  return request({
    url: "/admin/dish/add",
    method: "post",
    data,
  });
}

export function updateDish(data) {
  return request({
    url: "/admin/dish/update",
    method: "post",
    data,
  });
}

export function deleteDish(id) {
  return request({
    url: `/admin/dish/delete/${id}`,
    method: "delete",
  });
}

export function toggleDish(id) {
  return request({
    url: `/admin/dish/toggle/${id}`,
    method: "post",
  });
}
