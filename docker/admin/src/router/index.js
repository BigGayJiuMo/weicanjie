import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "../store/user";

// 页面组件
import Login from "../views/Login.vue";
import Layout from "../layout/Layout.vue";
import Dashboard from "../views/Dashboard.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // 登录页（无需 Layout）
    {
      path: "/login",
      name: "Login",
      component: Login,
    },

    // 后台主框架
    {
      path: "/",
      component: Layout,
      redirect: "/dashboard",
      children: [
        {
          path: "dashboard",
          name: "Dashboard",
          component: Dashboard,
        },

        //  餐厅管理（super / merchant）
        {
          path: "restaurant",
          name: "Restaurant",
          component: () =>
            import("../views/admin/restaurant/RestaurantList.vue"),
          meta: { role: ["super", "merchant"] }
        },
        
        // 餐厅分类（super 专用）
        {
          path: "restaurant-category",
          name: "RestaurantCategory",
          component: () => import("../views/admin/restaurant/RestaurantCategoryList.vue"),
          meta: { role: "super" }   // 只有超级管理员能管理分类
        },

        //  评价回复（super / merchant）
        {
          path: "review",
          name: "Review",
          component: () => import("../views/admin/review/ReviewList.vue"),
          meta: { role: ["super", "merchant"] }
        },

        //  评价审核（super 专用）
        {
          path: "review-audit",
          name: "ReviewAudit",
          component: () => import("../views/admin/review/ReviewAudit.vue"),
          meta: { role: "super" }
        },
        
        //  评价举报审核（super 专用）
        {
          path: "review-report",
          name: "ReviewReport",
          component: () => import("../views/admin/review/ReviewReportAudit.vue"),
          meta: { role: "super" }
        },

        //  菜品分类（super / merchant）
        {
          path: "dish-category",
          name: "DishCategory",
          component: () => import("../views/admin/dish/CategoryList.vue"),
          meta: { role: ["super", "merchant"] }
        },

        //  菜品管理（super / merchant）
        {
          path: "dish",
          name: "Dish",
          component: () => import("../views/admin/dish/DishList.vue"),
          meta: { role: ["super", "merchant"] }
        },

        //  订单管理（super / merchant）
        {
          path: "order",
          name: "Order",
          component: () => import("../views/admin/order/OrderList.vue"),
          meta: { role: ["super", "merchant"] }
        },

        //  后厨看板（super / merchant / kitchen）
        {
          path: "kitchen",
          name: "Kitchen",
          component: () => import("../views/admin/kitchen/KitchenBoard.vue"),
        },

        //  统计报表（super / merchant）
        {
          path: "report",
          name: "Report",
          component: () => import("../views/admin/report/Report.vue"),
          meta: { role: ["super", "merchant"] },
        },

        //  账号管理（super / merchant / kitchen）
        {
          path: "account-management",
          name: "AccountManagement",
          component: () => import("../views/admin/AccountManagement.vue"),
        },
      ],
    },
  ],
});

// ===================== 登录 & 权限守卫 =====================
router.beforeEach((to, from, next) => {
  const userStore = useUserStore();
  const token = userStore.token;
  const role = userStore.role;

  const isLoginPage = to.path === "/login";

  // 未登录跳转登录页
  if (!token && !isLoginPage) {
    return next("/login");
  }

  // 已登录不能访问登录页
  if (token && isLoginPage) {
    return next("/dashboard");
  }

  // ===== 统一处理 meta 权限 =====
  if (to.meta.role) {
    const allowRoles = Array.isArray(to.meta.role) ? to.meta.role : [to.meta.role];
    if (!allowRoles.includes(role)) {
      return next("/dashboard");
    }
  }

  // ===== kitchen 仅允许后厨看板 =====
  if (role === "kitchen") {
    const allow = ["/account-management", "/kitchen"];
    if (!allow.includes(to.path)) {
      return next("/kitchen");
    }
  }

  // ===== merchant 禁止访问“超级管理员页面” =====
  const superOnly = ["/restaurant-category", "/review-audit", "/review-report"];
  if (superOnly.includes(to.path) && role !== "super") {
    return next("/dashboard");
  }
  
  next();
});

export default router;
