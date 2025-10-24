// 全局状态管理
const AppState = {
    currentUser: null,
    isAdmin: false,
    currentSection: 'login'
};

// API基础URL - 需要根据实际后端配置调整
const API_BASE_URL = 'http://localhost:8080/api';

// 工具函数
const Utils = {
    // 显示消息提示
    showToast(message, type = 'info') {
        const toast = document.getElementById('messageToast');
        toast.textContent = message;
        toast.className = `toast ${type} show`;
        
        setTimeout(() => {
            toast.classList.remove('show');
        }, 3000);
    },

    // 发送API请求
    async apiRequest(endpoint, method = 'GET', data = null) {
        try {
            const options = {
                method,
                headers: {
                    'Content-Type': 'application/json',
                }
            };

            if (data) {
                options.body = JSON.stringify(data);
            }

            const response = await fetch(`${API_BASE_URL}${endpoint}`, options);
            const result = await response.json();
            
            if (!response.ok) {
                throw new Error(result.message || '请求失败');
            }
            
            return result;
        } catch (error) {
            console.error('API请求错误:', error);
            this.showToast(error.message, 'error');
            throw error;
        }
    },

    // 格式化日期
    formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('zh-CN');
    },

    // 验证表单
    validateForm(formData, requiredFields) {
        for (const field of requiredFields) {
            if (!formData[field] || formData[field].toString().trim() === '') {
                this.showToast(`请填写${field}`, 'warning');
                return false;
            }
        }
        return true;
    }
};

// 导航管理
const Navigation = {
    // 显示指定部分
    showSection(sectionId) {
        // 隐藏所有部分
        document.querySelectorAll('.content-section').forEach(section => {
            section.style.display = 'none';
            section.classList.remove('active');
        });

        // 显示目标部分
        const targetSection = document.getElementById(sectionId);
        if (targetSection) {
            targetSection.style.display = 'block';
            targetSection.classList.add('active');
        }

        // 更新导航链接状态
        document.querySelectorAll('.nav-link').forEach(link => {
            link.classList.remove('active');
        });

        const activeLink = document.querySelector(`[data-section="${sectionId}"]`);
        if (activeLink) {
            activeLink.classList.add('active');
        }

        AppState.currentSection = sectionId;
    },

    // 初始化导航
    init() {
        // 绑定导航链接点击事件
        document.querySelectorAll('.nav-link').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const sectionId = link.getAttribute('data-section');
                this.showSection(sectionId);
            });
        });

        // 默认显示登录页面
        this.showSection('login');
    }
};

// 用户管理
const UserManager = {
    // 登录
    async login(userId, password) {
        try {
            const result = await Utils.apiRequest('/login', 'POST', {
                userId: parseInt(userId),
                password: password
            });

            AppState.currentUser = result.user;
            AppState.isAdmin = result.user.privilege >= 2; // 假设2是管理员权限

            // 更新UI
            this.updateUserInterface();
            Utils.showToast('登录成功', 'success');
            
            // 根据权限显示相应页面
            if (AppState.isAdmin) {
                Navigation.showSection('add-train');
            } else {
                Navigation.showSection('query-remaining');
            }

            return true;
        } catch (error) {
            Utils.showToast('登录失败: ' + error.message, 'error');
            return false;
        }
    },

    // 注册
    async register(userId, username, password) {
        try {
            await Utils.apiRequest('/register', 'POST', {
                userId: parseInt(userId),
                username: username,
                password: password
            });

            Utils.showToast('注册成功', 'success');
            Navigation.showSection('login');
            return true;
        } catch (error) {
            Utils.showToast('注册失败: ' + error.message, 'error');
            return false;
        }
    },

    // 登出
    logout() {
        AppState.currentUser = null;
        AppState.isAdmin = false;
        this.updateUserInterface();
        Navigation.showSection('login');
        Utils.showToast('已登出', 'info');
    },

    // 更新用户界面
    updateUserInterface() {
        const currentUserSpan = document.getElementById('currentUser');
        const loginBtn = document.getElementById('loginBtn');
        const logoutBtn = document.getElementById('logoutBtn');

        if (AppState.currentUser) {
            currentUserSpan.textContent = `${AppState.currentUser.username} (ID: ${AppState.currentUser.userId})`;
            loginBtn.style.display = 'none';
            logoutBtn.style.display = 'inline-block';
        } else {
            currentUserSpan.textContent = '未登录';
            loginBtn.style.display = 'inline-block';
            logoutBtn.style.display = 'none';
        }

        // 更新管理员权限显示
        document.querySelectorAll('.admin-only').forEach(element => {
            if (AppState.isAdmin) {
                element.classList.add('show');
            } else {
                element.classList.remove('show');
            }
        });
    },

    // 查询用户信息
    async queryUser(userId) {
        try {
            const result = await Utils.apiRequest(`/user/${userId}`, 'GET');
            const resultDiv = document.getElementById('userQueryResult');
            resultDiv.innerHTML = `
                <div class="card">
                    <h3>用户信息</h3>
                    <p><strong>用户ID:</strong> ${result.userId}</p>
                    <p><strong>用户名:</strong> ${result.username}</p>
                    <p><strong>权限:</strong> ${result.privilege}</p>
                </div>
            `;
        } catch (error) {
            document.getElementById('userQueryResult').innerHTML = 
                `<div class="result-area"><p style="color: red;">查询失败: ${error.message}</p></div>`;
        }
    },

    // 修改用户权限
    async modifyPrivilege(userId, newPrivilege) {
        try {
            await Utils.apiRequest(`/user/${userId}/privilege`, 'PUT', {
                privilege: parseInt(newPrivilege)
            });
            Utils.showToast('权限修改成功', 'success');
        } catch (error) {
            Utils.showToast('权限修改失败: ' + error.message, 'error');
        }
    },

    // 修改密码
    async modifyPassword(userId, newPassword) {
        try {
            await Utils.apiRequest(`/user/${userId}/password`, 'PUT', {
                password: newPassword
            });
            Utils.showToast('密码修改成功', 'success');
        } catch (error) {
            Utils.showToast('密码修改失败: ' + error.message, 'error');
        }
    }
};

// 列车管理
const TrainManager = {
    // 添加列车
    async addTrain(trainId, seatNum, stationCount, stations, durations, prices) {
        try {
            const stationArray = stations.split('/');
            const durationArray = durations.split('/').map(d => parseInt(d));
            const priceArray = prices.split('/').map(p => parseInt(p));

            await Utils.apiRequest('/train', 'POST', {
                trainId: trainId,
                seatNum: parseInt(seatNum),
                stationCount: parseInt(stationCount),
                stations: stationArray,
                durations: durationArray,
                prices: priceArray
            });

            Utils.showToast('列车添加成功', 'success');
            document.getElementById('addTrainForm').reset();
        } catch (error) {
            Utils.showToast('列车添加失败: ' + error.message, 'error');
        }
    },

    // 查询列车
    async queryTrain(trainId) {
        try {
            const result = await Utils.apiRequest(`/train/${trainId}`, 'GET');
            const resultDiv = document.getElementById('trainQueryResult');
            
            let html = '<div class="card"><h3>列车信息</h3>';
            html += `<p><strong>车次ID:</strong> ${result.trainId}</p>`;
            html += `<p><strong>席位数:</strong> ${result.seatNum}</p>`;
            html += `<p><strong>站点数:</strong> ${result.stationCount}</p>`;
            
            if (result.stations && result.stations.length > 0) {
                html += '<p><strong>站点:</strong> ' + result.stations.join(' → ') + '</p>';
            }
            
            html += '</div>';
            resultDiv.innerHTML = html;
        } catch (error) {
            document.getElementById('trainQueryResult').innerHTML = 
                `<div class="result-area"><p style="color: red;">查询失败: ${error.message}</p></div>`;
        }
    }
};

// 票务管理
const TicketManager = {
    // 发布车票
    async releaseTicket(trainId, date) {
        try {
            await Utils.apiRequest('/ticket/release', 'POST', {
                trainId: trainId,
                date: date
            });
            Utils.showToast('车票发布成功', 'success');
        } catch (error) {
            Utils.showToast('车票发布失败: ' + error.message, 'error');
        }
    },

    // 过期车票
    async expireTicket(trainId, date) {
        try {
            await Utils.apiRequest('/ticket/expire', 'POST', {
                trainId: trainId,
                date: date
            });
            Utils.showToast('车票已过期', 'success');
        } catch (error) {
            Utils.showToast('车票过期操作失败: ' + error.message, 'error');
        }
    },

    // 查询余票
    async queryRemaining(trainId, date, departureStation) {
        try {
            const result = await Utils.apiRequest('/ticket/remaining', 'GET', null, {
                trainId: trainId,
                date: date,
                departureStation: departureStation
            });
            
            const resultDiv = document.getElementById('remainingResult');
            resultDiv.innerHTML = `
                <div class="card">
                    <h3>余票信息</h3>
                    <p><strong>车次:</strong> ${result.trainId}</p>
                    <p><strong>日期:</strong> ${result.date}</p>
                    <p><strong>出发站:</strong> ${result.departureStation}</p>
                    <p><strong>余票数量:</strong> <span class="status success">${result.remainingSeats}</span></p>
                </div>
            `;
        } catch (error) {
            document.getElementById('remainingResult').innerHTML = 
                `<div class="result-area"><p style="color: red;">查询失败: ${error.message}</p></div>`;
        }
    },

    // 购票
    async buyTicket(trainId, date, departureStation) {
        try {
            await Utils.apiRequest('/ticket/buy', 'POST', {
                trainId: trainId,
                date: date,
                departureStation: departureStation
            });
            Utils.showToast('购票成功', 'success');
        } catch (error) {
            Utils.showToast('购票失败: ' + error.message, 'error');
        }
    },

    // 查询订单
    async queryOrders() {
        try {
            const result = await Utils.apiRequest('/ticket/orders', 'GET');
            const resultDiv = document.getElementById('ordersResult');
            
            if (result.orders && result.orders.length > 0) {
                let html = '<table><thead><tr><th>车次</th><th>日期</th><th>出发站</th><th>状态</th></tr></thead><tbody>';
                result.orders.forEach(order => {
                    html += `<tr>
                        <td>${order.trainId}</td>
                        <td>${order.date}</td>
                        <td>${order.departureStation}</td>
                        <td><span class="status success">已购买</span></td>
                    </tr>`;
                });
                html += '</tbody></table>';
                resultDiv.innerHTML = html;
            } else {
                resultDiv.innerHTML = '<div class="result-area"><p>暂无订单</p></div>';
            }
        } catch (error) {
            document.getElementById('ordersResult').innerHTML = 
                `<div class="result-area"><p style="color: red;">查询失败: ${error.message}</p></div>`;
        }
    },

    // 退票
    async refundTicket(trainId, date, departureStation) {
        try {
            await Utils.apiRequest('/ticket/refund', 'POST', {
                trainId: trainId,
                date: date,
                departureStation: departureStation
            });
            Utils.showToast('退票成功', 'success');
        } catch (error) {
            Utils.showToast('退票失败: ' + error.message, 'error');
        }
    }
};

// 路线查询
const RouteManager = {
    // 显示路线
    async displayRoute(startStation, endStation) {
        try {
            const result = await Utils.apiRequest('/route/display', 'GET', null, {
                startStation: startStation,
                endStation: endStation
            });
            
            const resultDiv = document.getElementById('routeResult');
            resultDiv.innerHTML = `
                <div class="card">
                    <h3>路线信息</h3>
                    <p><strong>起点:</strong> ${result.startStation}</p>
                    <p><strong>终点:</strong> ${result.endStation}</p>
                    <p><strong>路线:</strong> ${result.routes.join(' → ')}</p>
                </div>
            `;
        } catch (error) {
            document.getElementById('routeResult').innerHTML = 
                `<div class="result-area"><p style="color: red;">查询失败: ${error.message}</p></div>`;
        }
    },

    // 最佳路径
    async findBestPath(startStation, endStation, preference) {
        try {
            const result = await Utils.apiRequest('/route/best', 'GET', null, {
                startStation: startStation,
                endStation: endStation,
                preference: preference
            });
            
            const resultDiv = document.getElementById('bestPathResult');
            resultDiv.innerHTML = `
                <div class="card">
                    <h3>最佳路径</h3>
                    <p><strong>起点:</strong> ${result.startStation}</p>
                    <p><strong>终点:</strong> ${result.endStation}</p>
                    <p><strong>路径:</strong> ${result.path.join(' → ')}</p>
                    <p><strong>总时间:</strong> ${result.totalTime}分钟</p>
                    <p><strong>总价格:</strong> ${result.totalPrice}元</p>
                </div>
            `;
        } catch (error) {
            document.getElementById('bestPathResult').innerHTML = 
                `<div class="result-area"><p style="color: red;">查询失败: ${error.message}</p></div>`;
        }
    },

    // 可达性查询
    async checkAccessibility(startStation, endStation) {
        try {
            const result = await Utils.apiRequest('/route/accessibility', 'GET', null, {
                startStation: startStation,
                endStation: endStation
            });
            
            const resultDiv = document.getElementById('accessibilityResult');
            const status = result.accessible ? 'success' : 'error';
            const statusText = result.accessible ? '可达' : '不可达';
            
            resultDiv.innerHTML = `
                <div class="card">
                    <h3>可达性查询结果</h3>
                    <p><strong>起点:</strong> ${result.startStation}</p>
                    <p><strong>终点:</strong> ${result.endStation}</p>
                    <p><strong>状态:</strong> <span class="status ${status}">${statusText}</span></p>
                </div>
            `;
        } catch (error) {
            document.getElementById('accessibilityResult').innerHTML = 
                `<div class="result-area"><p style="color: red;">查询失败: ${error.message}</p></div>`;
        }
    }
};

// 事件绑定
const EventHandlers = {
    // 初始化所有事件
    init() {
        // 登录表单
        document.getElementById('loginForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const userId = formData.get('userId');
            const password = formData.get('password');
            
            if (Utils.validateForm({userId, password}, ['userId', 'password'])) {
                await UserManager.login(userId, password);
            }
        });

        // 注册表单
        document.getElementById('registerForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const userId = formData.get('userId');
            const username = formData.get('username');
            const password = formData.get('password');
            
            if (Utils.validateForm({userId, username, password}, ['userId', 'username', 'password'])) {
                await UserManager.register(userId, username, password);
            }
        });

        // 登出按钮
        document.getElementById('logoutBtn').addEventListener('click', () => {
            UserManager.logout();
        });

        // 用户管理
        document.getElementById('queryUserBtn').addEventListener('click', async () => {
            const userId = document.getElementById('queryUserId').value;
            if (userId) {
                await UserManager.queryUser(userId);
            } else {
                Utils.showToast('请输入用户ID', 'warning');
            }
        });

        document.getElementById('modifyPrivilegeBtn').addEventListener('click', async () => {
            const userId = document.getElementById('modifyUserId').value;
            const newPrivilege = document.getElementById('newPrivilege').value;
            if (userId && newPrivilege) {
                await UserManager.modifyPrivilege(userId, newPrivilege);
            } else {
                Utils.showToast('请填写完整信息', 'warning');
            }
        });

        // 列车管理
        document.getElementById('addTrainForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const trainId = formData.get('trainId');
            const seatNum = formData.get('seatNum');
            const stationCount = formData.get('stationCount');
            const stations = formData.get('stations');
            const durations = formData.get('durations');
            const prices = formData.get('prices');
            
            if (Utils.validateForm({trainId, seatNum, stationCount, stations, durations, prices}, 
                ['trainId', 'seatNum', 'stationCount', 'stations', 'durations', 'prices'])) {
                await TrainManager.addTrain(trainId, seatNum, stationCount, stations, durations, prices);
            }
        });

        document.getElementById('queryTrainForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const trainId = formData.get('trainId');
            if (trainId) {
                await TrainManager.queryTrain(trainId);
            } else {
                Utils.showToast('请输入车次ID', 'warning');
            }
        });

        // 票务管理
        document.getElementById('releaseTicketForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const trainId = formData.get('trainId');
            const date = formData.get('date');
            
            if (Utils.validateForm({trainId, date}, ['trainId', 'date'])) {
                await TicketManager.releaseTicket(trainId, date);
            }
        });

        document.getElementById('expireTicketForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const trainId = formData.get('trainId');
            const date = formData.get('date');
            
            if (Utils.validateForm({trainId, date}, ['trainId', 'date'])) {
                await TicketManager.expireTicket(trainId, date);
            }
        });

        document.getElementById('queryRemainingForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const trainId = formData.get('trainId');
            const date = formData.get('date');
            const departureStation = formData.get('departureStation');
            
            if (Utils.validateForm({trainId, date, departureStation}, ['trainId', 'date', 'departureStation'])) {
                await TicketManager.queryRemaining(trainId, date, departureStation);
            }
        });

        document.getElementById('buyTicketForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const trainId = formData.get('trainId');
            const date = formData.get('date');
            const departureStation = formData.get('departureStation');
            
            if (Utils.validateForm({trainId, date, departureStation}, ['trainId', 'date', 'departureStation'])) {
                await TicketManager.buyTicket(trainId, date, departureStation);
            }
        });

        document.getElementById('queryOrdersBtn').addEventListener('click', async () => {
            await TicketManager.queryOrders();
        });

        document.getElementById('refundTicketForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const trainId = formData.get('trainId');
            const date = formData.get('date');
            const departureStation = formData.get('departureStation');
            
            if (Utils.validateForm({trainId, date, departureStation}, ['trainId', 'date', 'departureStation'])) {
                await TicketManager.refundTicket(trainId, date, departureStation);
            }
        });

        // 路线查询
        document.getElementById('displayRouteForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const startStation = formData.get('startStation');
            const endStation = formData.get('endStation');
            
            if (Utils.validateForm({startStation, endStation}, ['startStation', 'endStation'])) {
                await RouteManager.displayRoute(startStation, endStation);
            }
        });

        document.getElementById('bestPathForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const startStation = formData.get('startStation');
            const endStation = formData.get('endStation');
            const preference = formData.get('preference');
            
            if (Utils.validateForm({startStation, endStation}, ['startStation', 'endStation'])) {
                await RouteManager.findBestPath(startStation, endStation, preference);
            }
        });

        document.getElementById('accessibilityForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            const startStation = formData.get('startStation');
            const endStation = formData.get('endStation');
            
            if (Utils.validateForm({startStation, endStation}, ['startStation', 'endStation'])) {
                await RouteManager.checkAccessibility(startStation, endStation);
            }
        });
    }
};

// 应用初始化
document.addEventListener('DOMContentLoaded', () => {
    Navigation.init();
    EventHandlers.init();
    UserManager.updateUserInterface();
    
    console.log('火车票务管理系统前端已加载');
});
