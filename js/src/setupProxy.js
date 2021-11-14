const {createProxyMiddleware} = require("http-proxy-middleware");

module.exports = app => {
    const middleware = createProxyMiddleware({
        target: "http://localhost:8080",
        changeOrigin: true,
    });

    app.use("/api", middleware);
    app.use("/auth", middleware);
};
