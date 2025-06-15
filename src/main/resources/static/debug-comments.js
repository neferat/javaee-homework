// 评论功能调试工具
console.log('=== 评论调试工具加载 ===');

// 调试函数：检查API响应
async function debugCommentsAPI(postId = 1) {
    console.log(`\n=== 调试帖子${postId}的评论API ===`);
    
    try {
        const url = `/api/posts/${postId}/comments`;
        console.log('请求URL:', url);
        
        const response = await fetch(url, {
            credentials: 'include',
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });
        
        console.log('响应状态:', response.status);
        console.log('响应头:', response.headers);
        
        const text = await response.text();
        console.log('原始响应文本:', text);
        
        try {
            const result = JSON.parse(text);
            console.log('解析后的JSON:', result);
            
            if (result.code === 200) {
                const comments = result.data || [];
                console.log(`成功获取${comments.length}条评论`);
                
                comments.forEach((comment, index) => {
                    console.log(`评论${index + 1}:`, {
                        commentId: comment.commentId,
                        content: comment.content,
                        username: comment.username,
                        userAvatar: comment.userAvatar,
                        createdAt: comment.createdAt,
                        userId: comment.userId,
                        postId: comment.postId
                    });
                });
                
                return comments;
            } else {
                console.error('API错误:', result.message);
                return null;
            }
        } catch (parseError) {
            console.error('JSON解析失败:', parseError);
            return null;
        }
        
    } catch (error) {
        console.error('请求失败:', error);
        return null;
    }
}

// 调试函数：检查DOM元素
function debugCommentDOM() {
    console.log('\n=== 调试评论DOM元素 ===');
    
    // 检查模态框
    const modal = document.getElementById('postModal');
    console.log('帖子模态框:', modal);
    
    if (modal) {
        // 检查评论相关元素
        const commentsList = modal.querySelector('#modalCommentsList');
        const commentsLoading = modal.querySelector('.comments-loading');
        const commentsHeader = modal.querySelector('.comments-header');
        
        console.log('评论列表容器:', commentsList);
        console.log('加载指示器:', commentsLoading);
        console.log('评论标题:', commentsHeader);
        
        if (commentsList) {
            console.log('评论列表内容:', commentsList.innerHTML);
            console.log('评论项数量:', commentsList.querySelectorAll('.modal-comment-item').length);
        }
    }
    
    // 检查评论模态框（独立的）
    const commentModal = document.getElementById('commentModal');
    console.log('独立评论模态框:', commentModal);
}

// 调试函数：测试评论渲染
function debugCommentRendering() {
    console.log('\n=== 调试评论渲染 ===');
    
    // 创建测试数据
    const testComments = [
        {
            commentId: 1,
            content: '这是第一条测试评论',
            username: '测试用户1',
            userAvatar: 'https://via.placeholder.com/32',
            createdAt: new Date().toISOString(),
            userId: 1,
            postId: 1
        },
        {
            commentId: 2,
            content: '这是第二条测试评论，内容比较长一些，用来测试样式是否正常显示。',
            username: '测试用户2',
            userAvatar: null, // 测试空头像
            createdAt: new Date(Date.now() - 3600000).toISOString(), // 1小时前
            userId: 2,
            postId: 1
        }
    ];
    
    console.log('测试数据:', testComments);
    
    // 测试评论元素创建
    try {
        testComments.forEach((comment, index) => {
            console.log(`\n创建测试评论元素${index + 1}:`);
            
            // 检查函数是否存在
            if (typeof createModalCommentElement === 'function') {
                const html = createModalCommentElement(comment);
                console.log('生成的HTML:', html.substring(0, 200) + '...');
            } else {
                console.error('createModalCommentElement 函数不存在！');
            }
        });
    } catch (error) {
        console.error('评论元素创建失败:', error);
    }
    
    // 找到评论容器并测试渲染
    const container = document.getElementById('modalCommentsList');
    if (container && typeof renderModalComments === 'function') {
        console.log('\n测试渲染到实际容器:');
        renderModalComments(testComments, container);
    } else {
        console.error('容器或渲染函数不存在');
    }
}

// 调试函数：检查CSS样式
function debugCommentStyles() {
    console.log('\n=== 调试评论样式 ===');
    
    // 检查关键CSS类是否存在
    const testDiv = document.createElement('div');
    testDiv.className = 'modal-comment-item';
    document.body.appendChild(testDiv);
    
    const styles = window.getComputedStyle(testDiv);
    console.log('modal-comment-item样式:', {
        display: styles.display,
        padding: styles.padding,
        border: styles.border,
        visibility: styles.visibility
    });
    
    document.body.removeChild(testDiv);
    
    // 检查CSS文件是否加载
    const stylesheets = Array.from(document.styleSheets);
    console.log('已加载的样式表:', stylesheets.length);
    stylesheets.forEach((sheet, index) => {
        try {
            console.log(`样式表${index}:`, sheet.href || 'inline');
        } catch (e) {
            console.log(`样式表${index}: 无法访问（可能是跨域）`);
        }
    });
}

// 主调试函数
async function debugComments() {
    console.log('\n🔍 开始评论功能完整调试...');
    
    // 1. 检查API
    console.log('\n1️⃣ 检查API响应');
    const comments = await debugCommentsAPI(1);
    
    // 2. 检查DOM
    console.log('\n2️⃣ 检查DOM元素');
    debugCommentDOM();
    
    // 3. 检查渲染
    console.log('\n3️⃣ 检查渲染功能');
    debugCommentRendering();
    
    // 4. 检查样式
    console.log('\n4️⃣ 检查CSS样式');
    debugCommentStyles();
    
    console.log('\n✅ 调试完成！请查看控制台输出。');
    
    return {
        apiComments: comments,
        domElements: {
            modal: !!document.getElementById('postModal'),
            commentsList: !!document.getElementById('modalCommentsList'),
            commentsLoading: !!document.querySelector('.comments-loading')
        }
    };
}

// 工具函数：快速测试评论功能
async function quickTestComments() {
    console.log('\n🚀 快速测试评论功能...');
    
    // 打开第一个帖子的模态框
    const firstPost = document.querySelector('.card');
    if (firstPost) {
        console.log('找到第一个帖子，模拟点击...');
        firstPost.click();
        
        // 等待模态框打开
        setTimeout(() => {
            console.log('检查模态框是否打开...');
            const modal = document.getElementById('postModal');
            if (modal && modal.style.display !== 'none') {
                console.log('模态框已打开，检查评论区域...');
                debugCommentDOM();
            } else {
                console.log('模态框未打开');
            }
        }, 1000);
    } else {
        console.log('未找到帖子卡片');
    }
}

// 将调试函数添加到全局作用域
window.debugComments = debugComments;
window.debugCommentsAPI = debugCommentsAPI;
window.quickTestComments = quickTestComments;

console.log('评论调试工具已加载！使用方法：');
console.log('- debugComments() : 完整调试');
console.log('- debugCommentsAPI(postId) : 调试API');
console.log('- quickTestComments() : 快速测试'); 