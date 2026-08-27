import http from "./request";

export default {
  // 前台
  list(params) {
    return http.get("/review/list", { params });
  },
  userReviews(userId) {
    return http.get("/review/userReviews", { params: { userId } });
  },

  // 商家回复
  reply(reviewId, replyContent) {
    return http.post("/admin/merchant/review/reply", null, {
      params: { reviewId, replyContent }
    });
  },
  deleteReply(reviewId) {
    return http.post("/admin/merchant/review/reply/delete", null, {
      params: { reviewId }
    });
  },

  // 超管审核
  adminList(params) {
    return http.get("/admin/review/list", { params });
  },
  audit(params) {
    return http.post("/admin/review/audit", null, { params });
  },
  // 举报审核
  reportList(params) {
    return http.get("/admin/review/report/list", { params });
  },
  reportAudit(params) {
    return http.post("/admin/review/report/audit", null, { params });
  },
  deleteReview(id) {
    return http.post(`/admin/review/delete/${id}`);
  }
};
