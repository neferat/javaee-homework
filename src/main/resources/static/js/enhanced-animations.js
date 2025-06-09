// 高级动画和交互效果
class EnhancedAnimations {
    constructor() {
        this.initializeAnimations();
        this.setupIntersectionObserver();
        this.setupParallaxEffects();
        this.setupHoverEffects();
        this.setupLoadingAnimations();
        this.setupModalAnimations();
        this.setupNavbarEffects();
        this.setupPostCardAnimations();
    }

    // 初始化动画
    initializeAnimations() {
        // 页面加载完成后的动画
        document.addEventListener('DOMContentLoaded', () => {
            this.animatePageLoad();
        });

        // 监听窗口滚动
        let ticking = false;
        window.addEventListener('scroll', () => {
            if (!ticking) {
                requestAnimationFrame(() => {
                    this.handleScroll();
                    ticking = false;
                });
                ticking = true;
            }
        });
    }

    // 页面加载动画
    animatePageLoad() {
        // 导航栏从上方滑入
        gsap.fromTo('.navbar', 
            { y: -100, opacity: 0 },
            { y: 0, opacity: 1, duration: 0.8, ease: 'power3.out' }
        );

        // 搜索框缩放动画
        gsap.fromTo('.search-container', 
            { scale: 0.8, opacity: 0 },
            { scale: 1, opacity: 1, duration: 0.6, delay: 0.2, ease: 'back.out(1.7)' }
        );

        // 分类菜单项逐个出现
        gsap.fromTo('.menu-item', 
            { y: 30, opacity: 0 },
            { y: 0, opacity: 1, duration: 0.5, stagger: 0.1, delay: 0.4, ease: 'power2.out' }
        );

        // 用户区域从右侧滑入
        gsap.fromTo('.user-section', 
            { x: 100, opacity: 0 },
            { x: 0, opacity: 1, duration: 0.6, delay: 0.3, ease: 'power3.out' }
        );

        // 添加帖子按钮弹性出现
        gsap.fromTo('.add-post-btn', 
            { scale: 0, rotation: 180 },
            { scale: 1, rotation: 0, duration: 0.8, delay: 1, ease: 'elastic.out(1, 0.5)' }
        );
    }

    // 设置Intersection Observer进行视差滚动
    setupIntersectionObserver() {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    this.animateElement(entry.target);
                }
            });
        }, {
            threshold: 0.1,
            rootMargin: '0px 0px -50px 0px'
        });

        // 观察所有帖子卡片
        const observePostCards = () => {
            document.querySelectorAll('.post-card').forEach(card => {
                observer.observe(card);
            });
        };

        // 初始观察
        observePostCards();

        // 当新帖子加载时重新观察
        const originalLoadPosts = window.loadPosts;
        if (originalLoadPosts) {
            window.loadPosts = async function(...args) {
                const result = await originalLoadPosts.apply(this, args);
                setTimeout(observePostCards, 100);
                return result;
            };
        }
    }

    // 动画化元素出现
    animateElement(element) {
        if (element.classList.contains('post-card')) {
            gsap.fromTo(element, 
                { y: 50, opacity: 0, scale: 0.95 },
                { y: 0, opacity: 1, scale: 1, duration: 0.6, ease: 'power2.out' }
            );
            
            // 为帖子卡片添加hover动画
            this.addCardHoverAnimation(element);
        }
    }

    // 设置视差效果
    setupParallaxEffects() {
        // 背景视差效果
        window.addEventListener('scroll', () => {
            const scrolled = window.pageYOffset;
            const rate = scrolled * -0.5;
            
            const background = document.querySelector('body::before');
            if (background) {
                document.body.style.backgroundPosition = `center ${rate}px`;
            }
        });
    }

    // 设置悬停效果
    setupHoverEffects() {
        // 搜索框聚焦效果
        const searchInput = document.querySelector('.search-input');
        if (searchInput) {
            searchInput.addEventListener('focus', () => {
                gsap.to(searchInput, {
                    scale: 1.02,
                    duration: 0.3,
                    ease: 'power2.out'
                });
            });

            searchInput.addEventListener('blur', () => {
                gsap.to(searchInput, {
                    scale: 1,
                    duration: 0.3,
                    ease: 'power2.out'
                });
            });
        }

        // 按钮悬停效果
        document.querySelectorAll('.btn').forEach(btn => {
            btn.addEventListener('mouseenter', () => {
                gsap.to(btn, {
                    scale: 1.05,
                    duration: 0.3,
                    ease: 'power2.out'
                });
            });

            btn.addEventListener('mouseleave', () => {
                gsap.to(btn, {
                    scale: 1,
                    duration: 0.3,
                    ease: 'power2.out'
                });
            });
        });

        // 分类菜单项悬停效果
        document.querySelectorAll('.menu-item').forEach(item => {
            item.addEventListener('mouseenter', () => {
                gsap.to(item, {
                    y: -3,
                    duration: 0.3,
                    ease: 'power2.out'
                });
            });

            item.addEventListener('mouseleave', () => {
                gsap.to(item, {
                    y: 0,
                    duration: 0.3,
                    ease: 'power2.out'
                });
            });
        });
    }

    // 设置加载动画
    setupLoadingAnimations() {
        const originalCreateLoading = () => {
            const loading = document.createElement('div');
            loading.className = 'loading active';
            loading.innerHTML = `
                <div class="loading-spinner"></div>
                <div class="loading-text">加载中...</div>
            `;
            document.body.appendChild(loading);

            // 动画化加载器出现
            gsap.fromTo(loading, 
                { scale: 0.8, opacity: 0 },
                { scale: 1, opacity: 1, duration: 0.4, ease: 'back.out(1.7)' }
            );

            return loading;
        };

        const originalRemoveLoading = (loading) => {
            gsap.to(loading, {
                scale: 0.8,
                opacity: 0,
                duration: 0.3,
                ease: 'power2.in',
                onComplete: () => {
                    if (loading.parentNode) {
                        loading.parentNode.removeChild(loading);
                    }
                }
            });
        };

        // 替换原有的加载动画函数
        window.createLoadingAnimation = originalCreateLoading;
        window.removeLoadingAnimation = originalRemoveLoading;
    }

    // 设置模态框动画
    setupModalAnimations() {
        // 监听模态框开启
        const observer = new MutationObserver((mutations) => {
            mutations.forEach((mutation) => {
                if (mutation.attributeName === 'class') {
                    const modal = mutation.target;
                    if (modal.classList.contains('modal')) {
                        if (modal.classList.contains('active')) {
                            this.animateModalOpen(modal);
                        }
                    }
                }
            });
        });

        document.querySelectorAll('.modal').forEach(modal => {
            observer.observe(modal, { attributes: true });
        });
    }

    // 模态框打开动画
    animateModalOpen(modal) {
        const content = modal.querySelector('.modal-content');
        if (content) {
            gsap.fromTo(content, 
                { scale: 0.7, y: 50, opacity: 0 },
                { scale: 1, y: 0, opacity: 1, duration: 0.5, ease: 'back.out(1.3)' }
            );
        }
    }

    // 设置导航栏效果
    setupNavbarEffects() {
        let lastScrollTop = 0;
        const navbar = document.querySelector('.navbar');

        window.addEventListener('scroll', () => {
            const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
            
            if (scrollTop > lastScrollTop && scrollTop > 100) {
                // 向下滚动，隐藏导航栏
                gsap.to(navbar, {
                    y: -navbar.offsetHeight,
                    duration: 0.3,
                    ease: 'power2.out'
                });
            } else {
                // 向上滚动，显示导航栏
                gsap.to(navbar, {
                    y: 0,
                    duration: 0.3,
                    ease: 'power2.out'
                });
            }
            
            lastScrollTop = scrollTop;
        });
    }

    // 设置帖子卡片动画
    setupPostCardAnimations() {
        // 添加点击波纹效果
        document.addEventListener('click', (e) => {
            if (e.target.closest('.post-card')) {
                this.createRippleEffect(e);
            }
        });
    }

    // 创建波纹效果
    createRippleEffect(e) {
        const card = e.target.closest('.post-card');
        const rect = card.getBoundingClientRect();
        const ripple = document.createElement('div');
        
        ripple.style.cssText = `
            position: absolute;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.3);
            transform: scale(0);
            animation: ripple 0.6s linear;
            pointer-events: none;
        `;

        const size = Math.max(rect.width, rect.height);
        ripple.style.width = ripple.style.height = size + 'px';
        ripple.style.left = (e.clientX - rect.left - size / 2) + 'px';
        ripple.style.top = (e.clientY - rect.top - size / 2) + 'px';

        card.style.position = 'relative';
        card.style.overflow = 'hidden';
        card.appendChild(ripple);

        setTimeout(() => {
            ripple.remove();
        }, 600);
    }

    // 添加卡片悬停动画
    addCardHoverAnimation(card) {
        card.addEventListener('mouseenter', () => {
            gsap.to(card, {
                y: -8,
                scale: 1.02,
                duration: 0.4,
                ease: 'power2.out'
            });

            // 图片缩放效果
            const image = card.querySelector('.post-image');
            if (image) {
                gsap.to(image, {
                    scale: 1.1,
                    duration: 0.4,
                    ease: 'power2.out'
                });
            }

            // 顶部渐变条动画
            const topBar = card.querySelector('::before');
            gsap.to(card, {
                '--gradient-width': '100%',
                duration: 0.4,
                ease: 'power2.out'
            });
        });

        card.addEventListener('mouseleave', () => {
            gsap.to(card, {
                y: 0,
                scale: 1,
                duration: 0.4,
                ease: 'power2.out'
            });

            // 图片还原
            const image = card.querySelector('.post-image');
            if (image) {
                gsap.to(image, {
                    scale: 1,
                    duration: 0.4,
                    ease: 'power2.out'
                });
            }
        });
    }

    // 处理滚动事件
    handleScroll() {
        const scrollY = window.scrollY;
        
        // 视差背景
        const background = document.querySelector('body::before');
        if (background) {
            gsap.set(background, {
                y: scrollY * 0.5
            });
        }

        // 添加帖子按钮跟随滚动
        const addBtn = document.querySelector('.add-post-btn');
        if (addBtn) {
            if (scrollY > 200) {
                gsap.to(addBtn, {
                    scale: 0.8,
                    duration: 0.3,
                    ease: 'power2.out'
                });
            } else {
                gsap.to(addBtn, {
                    scale: 1,
                    duration: 0.3,
                    ease: 'power2.out'
                });
            }
        }
    }

    // 平滑滚动到元素
    smoothScrollTo(element, duration = 1000) {
        gsap.to(window, {
            duration: duration / 1000,
            scrollTo: {
                y: element,
                offsetY: 100
            },
            ease: 'power2.inOut'
        });
    }

    // 文字打字机效果
    typeWriter(element, text, speed = 50) {
        let i = 0;
        element.innerHTML = '';
        
        function type() {
            if (i < text.length) {
                element.innerHTML += text.charAt(i);
                i++;
                setTimeout(type, speed);
            }
        }
        
        type();
    }

    // 数字动画计数
    animateNumber(element, start, end, duration = 2000) {
        gsap.to({ value: start }, {
            value: end,
            duration: duration / 1000,
            ease: 'power2.out',
            onUpdate: function() {
                element.textContent = Math.round(this.targets()[0].value);
            }
        });
    }

    // 添加粒子效果
    addParticleEffect(container) {
        const particleCount = 50;
        
        for (let i = 0; i < particleCount; i++) {
            const particle = document.createElement('div');
            particle.style.cssText = `
                position: absolute;
                width: 2px;
                height: 2px;
                background: rgba(102, 126, 234, 0.7);
                border-radius: 50%;
                pointer-events: none;
            `;
            
            container.appendChild(particle);
            
            gsap.set(particle, {
                x: Math.random() * container.offsetWidth,
                y: Math.random() * container.offsetHeight
            });
            
            gsap.to(particle, {
                y: -100,
                opacity: 0,
                duration: Math.random() * 3 + 2,
                ease: 'power1.out',
                repeat: -1,
                delay: Math.random() * 2
            });
        }
    }

    // 鼠标跟随效果
    setupMouseFollower() {
        const follower = document.createElement('div');
        follower.style.cssText = `
            position: fixed;
            width: 20px;
            height: 20px;
            background: radial-gradient(circle, rgba(102, 126, 234, 0.8) 0%, transparent 70%);
            border-radius: 50%;
            pointer-events: none;
            z-index: 9999;
            transform: translate(-50%, -50%);
            transition: opacity 0.3s ease;
        `;
        
        document.body.appendChild(follower);
        
        document.addEventListener('mousemove', (e) => {
            gsap.to(follower, {
                x: e.clientX,
                y: e.clientY,
                duration: 0.3,
                ease: 'power2.out'
            });
        });
        
        document.addEventListener('mouseenter', () => {
            gsap.to(follower, { opacity: 1, duration: 0.3 });
        });
        
        document.addEventListener('mouseleave', () => {
            gsap.to(follower, { opacity: 0, duration: 0.3 });
        });
    }
}

// 添加CSS动画样式
const style = document.createElement('style');
style.textContent = `
    @keyframes ripple {
        to {
            transform: scale(4);
            opacity: 0;
        }
    }
    
    .fade-in-up {
        animation: fadeInUp 0.6s ease-out;
    }
    
    @keyframes fadeInUp {
        from {
            opacity: 0;
            transform: translateY(30px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }
    
    .bounce-in {
        animation: bounceIn 0.8s ease-out;
    }
    
    @keyframes bounceIn {
        0% {
            opacity: 0;
            transform: scale(0.3);
        }
        50% {
            opacity: 1;
            transform: scale(1.05);
        }
        70% {
            transform: scale(0.9);
        }
        100% {
            opacity: 1;
            transform: scale(1);
        }
    }
    
    .slide-in-left {
        animation: slideInLeft 0.6s ease-out;
    }
    
    @keyframes slideInLeft {
        from {
            opacity: 0;
            transform: translateX(-50px);
        }
        to {
            opacity: 1;
            transform: translateX(0);
        }
    }
`;

document.head.appendChild(style);

// 初始化增强动画
document.addEventListener('DOMContentLoaded', () => {
    // 检查是否有GSAP库
    if (typeof gsap !== 'undefined') {
        new EnhancedAnimations();
    } else {
        console.warn('GSAP library not found. Some animations may not work.');
        // 提供降级方案
        document.querySelectorAll('.post-card').forEach(card => {
            card.classList.add('fade-in-up');
        });
    }
});

// 导出类以供其他脚本使用
window.EnhancedAnimations = EnhancedAnimations; 