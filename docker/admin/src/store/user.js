import { defineStore } from "pinia";

export const useUserStore = defineStore("user", {
  state: () => ({
    token: "",
    username: "",
    role: "",
    restaurantId: null,
    userId: null,
    phone: null,
  }),

  actions: {
    // 保存 Token
    setToken(token) {
      this.token = token;
      localStorage.setItem("token", token);
    },

    // 保存用户信息
    setUserInfo(data) {
      this.username = data.username;
      this.role = data.role;
      this.restaurantId = data.restaurantId;
      this.userId = data.id;
      this.phone = data.phone;

      localStorage.setItem("userInfo", JSON.stringify(data));
    },

    // 初始化（刷新后恢复）
    loadFromLocal() {
      const token = localStorage.getItem("token");
      const userInfo = localStorage.getItem("userInfo");

      if (token) this.token = token;
      if (userInfo) {
        const data = JSON.parse(userInfo);
        this.username = data.username;
        this.role = data.role;
        this.restaurantId = data.restaurantId;
        this.userId = data.id;
        this.phone = data.phone;
      }
    }
  }
});
