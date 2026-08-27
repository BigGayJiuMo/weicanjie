import http from "./request";

// 获取统计报表数据
export const getReportData = (
  startDate,
  endDate,
  restaurantId = null,
  granularity = "day"
) => {
  const formattedStartDate =
    startDate instanceof Date
      ? startDate.toISOString().split("T")[0]
      : startDate;

  const formattedEndDate =
    endDate instanceof Date
      ? endDate.toISOString().split("T")[0]
      : endDate;

  return http.get("/admin/report/data", {
    params: {
      startDate: formattedStartDate,
      endDate: formattedEndDate,
      restaurantId,     // 可选：餐厅联动
      granularity       // 可选：day / week / month
    }
  });
};
