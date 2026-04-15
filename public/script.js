cd "c:\Users\gamin\OneDrive\Desktop\End to End\End to End"
python server.py/* script.js - Updated with 3 Roles and Basic Backend Integration */

const API_BASE_URL = 'http://localhost:3000/api';

class SkillVerseApp {
    constructor() {
        this.user = JSON.parse(localStorage.getItem('user')) || null;
        this.theme = localStorage.getItem('theme') || 'light';
        this.currentRole = 'student'; // selected role at login
        this.courses = [];
        this.selectedCourseForPayment = null;
        this.init();
    }

    async init() {
        this.applyTheme();
        this.setupAuthUI();
        await this.syncCourses();
        this.setupDashboardVisibility();
        console.log("SkillVerse App 2.0 Initialized.");
    }

    async syncCourses() {
        try {
            const resp = await fetch(`${API_BASE_URL}/courses`);
            this.courses = await resp.json();
        } catch (err) {
            console.error("Backend offline, using local fallbacks.", err);
        }
    }

    applyTheme() {
        document.documentElement.setAttribute('data-theme', this.theme);
        const icon = document.getElementById('theme-icon');
        if (icon) {this.notifyn
            icon.innerHTML = this.theme === 'dark' ? 
                '<circle cx="12" cy="12" r="5"></circle><line x1="12" y1="1" x2="12" y2="3"></line><line x1="12" y1="21" x2="12" y2="23"></line><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line><line x1="1" y1="12" x2="3" y2="12"></line><line x1="21" y1="12" x2="23" y2="12"></line><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"></line><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"></line>' :
                '<path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>';
        }
    }

    toggleTheme() {
        this.theme = this.theme === 'light' ? 'dark' : 'light';
        localStorage.setItem('theme', this.theme);
        this.applyTheme();
    }

    setRole(role) {
        this.currentRole = role;
        document.querySelectorAll('.role-btn').forEach(btn => btn.classList.remove('active', 'btn-primary'));
        document.querySelectorAll('.role-btn').forEach(btn => btn.classList.add('btn-ghost'));
        const activeBtn = document.getElementById(`btn-role-${role}`);
        activeBtn.classList.remove('btn-ghost');
        activeBtn.classList.add('active', 'btn-primary');
        
        const emailInput = document.getElementById('login-email');
        const emailMap = { student: 'arjun@skillverse.in', admin: 'admin@skillverse.in', instructor: 'priya@skillverse.in' };
        emailInput.value = emailMap[role];
    }

    login() {
        const email = document.getElementById('login-email').value;
        this.performAuth(`${API_BASE_URL}/login`, { email, role: this.currentRole });
    }

    async register() {
        const name = document.getElementById('reg-name').value;
        const email = document.getElementById('reg-email').value;
        if (!name || !email) return this.notify("Please fill all fields", "error");
        await this.performAuth(`${API_BASE_URL}/register`, { name, email, role: this.currentRole });
    }

    async performAuth(url, body) {
        try {
            const resp = await fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });
            const data = await resp.json();
            if (data.success) {
                this.user = data.user;
                localStorage.setItem('user', JSON.stringify(this.user));
                this.setupAuthUI();
                this.setupDashboardVisibility();
                this.notify(`Success! Logged in as ${this.user.name}`);
            } else {
                this.notify(data.message || "Auth failed", "error");
            }
        } catch (err) {
            this.notify("Backend not connected.", "error");
        }
    }

    showRegister(show) {
        document.getElementById('login-form-box').classList.toggle('hidden', show);
        document.getElementById('register-form-box').classList.toggle('hidden', !show);
        document.querySelector('.login-card h1').textContent = show ? 'Join SkillVerse' : 'SkillVerse';
        document.querySelector('.login-card p').textContent = show ? 'Create your professional account' : 'Sign in to your account';
    }

    logout() {
        this.user = null;
        localStorage.removeItem('user');
        this.setupAuthUI();
        this.setupDashboardVisibility();
        this.notify("Logged out successfully.");
    }

    setupAuthUI() {
        const landing = document.getElementById('landing-login');
        const mainNav = document.getElementById('main-nav');
        const mainContent = document.getElementById('main-content');
        const userName = document.getElementById('user-name');
        const userBadge = document.getElementById('user-role-badge');

        if (this.user) {
            landing.classList.add('hidden');
            mainNav.classList.remove('hidden');
            mainContent.classList.remove('hidden');
            userName.textContent = this.user.name;
            userBadge.textContent = this.user.role.toUpperCase();
            userBadge.className = this.user.role === 'admin' ? 'admin-badge' : 'instructor-badge';
            if (this.user.role === 'student') userBadge.classList.add('hidden');
        } else {
            landing.classList.remove('hidden');
            mainNav.classList.add('hidden');
            mainContent.classList.add('hidden');
        }
    }

    setupDashboardVisibility() {
        if (!this.user) return;
        this.hideAllSections();
        if (this.user.role === 'admin') {
            this.switchView('admin');
        } else if (this.user.role === 'instructor') {
            this.switchView('instructor');
        } else {
            this.switchView('student');
        }
    }

    hideAllSections() {
        ['marketplace-section', 'admin-dashboard-section', 'instructor-dashboard-section', 'student-dashboard-section', 'learning-view']
            .forEach(id => document.getElementById(id).classList.add('hidden'));
    }

    switchView(view) {
        this.hideAllSections();
        const sectionId = { 
            admin: 'admin-dashboard-section', 
            student: 'student-dashboard-section', 
            instructor: 'instructor-dashboard-section',
            marketplace: 'marketplace-section',
            learning: 'learning-view'
        }[view];
        document.getElementById(sectionId).classList.remove('hidden');
        this.renderSpecificContent(view);
    }

    async renderSpecificContent(view) {
        await this.syncCourses();
        if (view === 'marketplace') this.renderMarketplace();
        if (view === 'admin') this.renderAdminTools();
        if (view === 'student') this.renderStudentJourney();
        if (view === 'instructor') this.renderInstructorTools();
    }

    renderMarketplace() {
        const grid = document.getElementById('course-grid');
        grid.innerHTML = '';
        this.courses.forEach(c => {
            const enrolled = this.user.enrolled && this.user.enrolled.includes(c.id);
            grid.innerHTML += `
                <div class="course-card glass">
                    <div class="course-image"><img src="${c.image}" style="width:100%; height:100%; object-fit:cover;"></div>
                    <div class="course-content">
                        <span class="course-category">${c.category}</span>
                        <h3 class="course-title">${c.title}</h3>
                        <div class="course-meta">
                            <span class="course-price">$${c.price}</span>
                            <button class="btn ${enrolled ? 'btn-ghost' : 'btn-primary'}" ${enrolled ? 'disabled' : `onclick="app.openPayment(${c.id})"`}>
                                ${enrolled ? 'Enrolled' : 'Register Now'}
                            </button>
                        </div>
                    </div>
                </div>`;
        });
    }

    async renderAdminTools() {
        const list = document.getElementById('admin-course-list');
        list.innerHTML = '';
        this.courses.forEach(c => {
            list.innerHTML += `
                <div class="glass" style="padding: 1.5rem; border-radius: 1rem; margin-bottom: 1rem; display: flex; justify-content: space-between; align-items: center;">
                    <div>
                        <span style="font-weight: 700; font-size: 1.1rem;">${c.title}</span><br>
                        <span style="color: var(--text-sub); font-size: 0.85rem;">Status: <b style="color: ${c.status === 'active' ? '#10b981' : '#f59e0b'}">${c.status}</b></span>
                    </div>
                    <div>
                        <button class="btn btn-ghost" onclick="app.adminDeleteCourse(${c.id})" style="color: #ef4444;">Delete</button>
                    </div>
                </div>`;
        });
    }

    async renderStudentJourney() {
        const grid = document.getElementById('student-course-grid');
        grid.innerHTML = '';
        const myCourses = this.courses.filter(c => this.user.enrolled && this.user.enrolled.includes(c.id));
        if (myCourses.length === 0) {
            grid.innerHTML = '<div class="glass" style="grid-column: 1/-1; padding: 4rem; text-align: center;"><h3>No courses yet. Start your journey!</h3><button class="btn btn-primary" onclick="app.switchView(\'marketplace\')">Explore Courses</button></div>';
            return;
        }
        myCourses.forEach(c => {
            const completedCount = this.user.completedModules[c.id] ? this.user.completedModules[c.id].length : 0;
            const progress = Math.round((completedCount / c.modules.length) * 100);
            
            grid.innerHTML += `
                <div class="course-card glass">
                    <div class="course-image"><img src="${c.image}" style="width:100%; height:100%; object-fit:cover;"></div>
                    <div class="course-content">
                        <h3 class="course-title">${c.title}</h3>
                        <div class="progress-container"><div class="progress-bar" style="width: ${progress}%;"></div></div>
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 1rem;">
                            <span style="font-size: 0.8rem; font-weight: 700;">Progress: ${progress}%</span>
                            <button class="btn ${progress === 100 ? 'btn-ghost' : 'btn-primary'}" onclick="app.openCoursePlayer(${c.id})">
                                ${progress === 100 ? 'Review Course' : 'Learn Now'}
                            </button>
                        </div>
                        ${progress === 100 ? `
                        <button class="btn btn-primary" style="width:100%; margin-top:1rem; background: linear-gradient(to right, #f59e0b, #ec4899);" onclick="app.showCertificate(${c.id})">
                            View Certificate 🎓
                        </button>` : ''}
                    </div>
                </div>`;
        });
    }

    async renderInstructorTools() {
        const grid = document.getElementById('instructor-course-grid');
        grid.innerHTML = '';
        const instructorCourses = this.courses.filter(c => c.instructorId === this.user.id);
        instructorCourses.forEach(c => {
            grid.innerHTML += `
                <div class="course-card glass">
                    <div class="course-content">
                        <div style="display:flex; justify-content: space-between; margin-bottom: 1rem;">
                            <h3>${c.title}</h3>
                            <span class="admin-badge" style="background:#10b98122; color:#10b981;">${c.status}</span>
                        </div>
                        <p style="margin-bottom: 1.5rem;"><b>Students registered:</b> ${c.students ? c.students.length : 0}</p>
                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap:0.5rem; margin-bottom: 0.5rem;">
                            <button class="btn btn-primary" onclick="app.updateCourseStatus(${c.id}, 'active')">Start Course</button>
                            <button class="btn btn-ghost" onclick="app.updateCourseStatus(${c.id}, 'ended')">End Course</button>
                        </div>
                        <button class="btn btn-ghost" style="width:100%;" onclick="app.viewStudents(${c.id})">View Students List</button>
                    </div>
                </div>`;
        });
    }

    // Role Specific Logic
    openPayment(courseId) {
        this.selectedCourseForPayment = this.courses.find(c => c.id === courseId);
        document.getElementById('payment-course-title').textContent = this.selectedCourseForPayment.title;
        document.getElementById('payment-modal').style.display = 'flex';
    }

    closePayment() {
        document.getElementById('payment-modal').style.display = 'none';
        this.selectedCourseForPayment = null;
    }

    async processPayment() {
        const resp = await fetch(`${API_BASE_URL}/courses/enroll`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ courseId: this.selectedCourseForPayment.id, userId: this.user.id })
        });
        const data = await resp.json();
        if (data.success) {
            this.user = data.user;
            localStorage.setItem('user', JSON.stringify(this.user));
            this.closePayment();
            this.notify("Registration Successful!");
            this.switchView('student');
        }
    }

    openCoursePlayer(courseId) {
        const course = this.courses.find(c => c.id === courseId);
        if (!course) return this.notify("Course not found", "error");

        document.getElementById('learning-title').textContent = course.title;
        document.getElementById('learning-instructor').textContent = `Instructor: ${course.instructor}`;
        
        const content = document.getElementById('learning-content');
        content.innerHTML = '<h3 style="margin-bottom:1rem;">Course Modules</h3><div class="module-list"></div>';
        
        const list = content.querySelector('.module-list');
        course.modules.forEach((mod, index) => {
            const modDiv = document.createElement('div');
            modDiv.className = 'module-item';
            modDiv.innerHTML = `
                <span>${index + 1}. ${mod.title}</span>
                <button class="btn btn-ghost start-mod-btn">Start</button>
            `;
            
            modDiv.querySelector('.start-mod-btn').addEventListener('click', () => {
                this.playModule(mod, courseId);
            });
            
            const isCompleted = this.user.completedModules[courseId] && this.user.completedModules[courseId].includes(mod.id);
            if (isCompleted) {
                modDiv.style.borderLeft = '4px solid #10b981';
                modDiv.querySelector('.start-mod-btn').textContent = 'Review';
                const status = document.createElement('span');
                status.innerHTML = ' ✅';
                modDiv.querySelector('span').appendChild(status);
            }
            
            list.appendChild(modDiv);
        });
        
        this.switchView('learning');
    }

    playModule(mod, courseId) {
        const player = document.querySelector('#learning-view .glass div div');
        player.innerHTML = `
            <div style="padding: 2rem;">
                <h2 style="color: var(--primary-500); margin-bottom: 1rem;">Now Playing: ${mod.title}</h2>
                <p style="color: white; font-size: 0.9rem;">${mod.content}</p>
                <div style="margin-top: 2rem; display: flex; gap: 1rem; align-items: center;">
                    <button class="btn btn-primary" id="complete-mod-btn">Mark as Completed ✅</button>
                    <span style="font-size: 0.8rem; color: #94a3b8;">Progress tracked for Arjun Kumar</span>
                </div>
            </div>
        `;
        
        document.getElementById('complete-mod-btn').addEventListener('click', () => {
            this.completeModule(mod.id, courseId);
        });

        this.notify(`Started: ${mod.title}`);
    }

    async completeModule(moduleId, courseId) {
        try {
            const resp = await fetch(`${API_BASE_URL}/courses/complete-module`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ courseId, moduleId, userId: this.user.id })
            });
            const data = await resp.json();
            if (data.success) {
                this.user = data.user;
                localStorage.setItem('user', JSON.stringify(this.user));
                this.notify("Module completed! Progress updated.");
                
                // Check if all completed for celebration
                const course = this.courses.find(c => c.id === courseId);
                const isAllDone = this.user.completedModules[courseId].length === course.modules.length;
                
                if (isAllDone) {
                    this.notify("CONGRATULATIONS! Course completed. 🎓", "success");
                    this.showCertificate(courseId);
                } else {
                    this.openCoursePlayer(courseId); // refresh list to show ticks
                }
            }
        } catch (err) {
            this.notify("Error saving progress", "error");
        }
    }

    showCertificate(courseId) {
        const course = this.courses.find(c => c.id === courseId);
        document.getElementById('cert-user-name').textContent = this.user.name;
        document.getElementById('cert-course-title').textContent = course.title;
        document.getElementById('certificate-modal').style.display = 'flex';
    }

    async adminCreateCourse() {
        const title = document.getElementById('adm-course-title').value;
        const cat = document.getElementById('adm-course-cat').value;
        const price = document.getElementById('adm-course-price').value;
        
        if (!title || !cat || !price) return this.notify("Please fill all fields", "error");

        const resp = await fetch(`${API_BASE_URL}/courses`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ title, category: cat, price, instructorId: 'inst_1', instructor: 'Sarah Jenkins', image: 'https://images.unsplash.com/photo-1542744173-8e7e53415bb0?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80', modules: [{title: 'Overview', content: 'Intro'}] })
        });
        if (resp.ok) {
            this.notify("New Course Launched Successfully!");
            document.getElementById('adm-course-title').value = '';
            this.renderAdminTools();
        }
    }

    async adminDeleteCourse(id) {
        if (!confirm("Are you sure you want to delete this course?")) return;
        const resp = await fetch(`${API_BASE_URL}/courses/${id}`, { method: 'DELETE' });
        if (resp.ok) {
            this.notify("Course deleted.");
            this.renderAdminTools();
        }
    }

    async updateCourseStatus(id, status) {
        const resp = await fetch(`${API_BASE_URL}/courses/${id}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status })
        });
        if (resp.ok) {
            this.notify(`Course is now ${status}`);
            this.renderInstructorTools();
        }
    }

    async viewStudents(courseId) {
        const resp = await fetch(`${API_BASE_URL}/instructor/students/${courseId}`);
        const students = await resp.json();
        const list = document.getElementById('students-list');
        list.innerHTML = '';
        if (students.length === 0) list.innerHTML = '<p>No students enrolled yet.</p>';
        students.forEach(s => {
            list.innerHTML += `<div class="glass" style="padding:1rem; margin-bottom:0.5rem; border-radius:1rem;"><b>${s.name}</b> (${s.email})</div>`;
        });
        document.getElementById('students-modal').style.display = 'flex';
    }

    showMarketplace() { this.switchView('marketplace'); }

    notify(message, type = "success") {
        const container = document.getElementById('notification-container');
        const notification = document.createElement('div');
        notification.className = 'glass';
        notification.style.cssText = `padding: 1rem 1.5rem; border-radius: 1rem; margin-bottom: 1rem; border-left: 4px solid ${type === 'success' ? 'var(--primary-500)' : '#ef4444'}; box-shadow: var(--shadow-lg); animation: slideLeft 0.3s ease; display: flex; align-items: center; gap: 1rem;`;
        notification.innerHTML = `<b>${type === 'success' ? 'System' : 'Error'}</b><span>${message}</span>`;
        container.appendChild(notification);
        setTimeout(() => {
            notification.style.opacity = '0';
            notification.style.transition = 'all 0.5s ease';
            setTimeout(() => notification.remove(), 500);
        }, 3000);
    }
}

const app = new SkillVerseApp();
window.app = app;
