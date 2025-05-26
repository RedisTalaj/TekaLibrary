document.addEventListener('DOMContentLoaded', function() {
    const chatbox = document.querySelector('.chatbox');
    const textarea = document.querySelector('.chat-input textarea');
    const sendBtn = document.getElementById('sendBTN');
    const chatboxContainer = document.querySelector('.chatbox-container');

    // Open/close chatbox
    document.querySelector('.assistent-img').addEventListener('click', function() {
        chatboxContainer.togglePopover();
    });

    // Send message function
    async function sendMessage() {
        const userMessage = textarea.value.trim();
        if (!userMessage) return;

        // Add user message to chat
        addChatMessage(userMessage, 'outgoing');
        textarea.value = '';

        // Show "typing" indicator
        const typingIndicator = addChatMessage('TekaLibrary is thinking...', 'incoming');

        try {
            // Call your recommendation API
            const response = await fetch('/api/recommendations', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    query: userMessage,
                    topK: 3 // Get top 3 recommendations
                })
            });

            if (!response.ok) throw new Error('API request failed');

            const data = await response.json();

            // Remove typing indicator
            typingIndicator.remove();

            // Add recommendations to chat
            if (data.length > 0) {
                let responseHtml = `<p>Here are some books you might like:</p><ul class="recommendations-list">`;

                data.forEach(book => {
                    responseHtml += `
                        <li class="recommendation-item">
                            <a href="/books/details/${book.bookId}" class="book-link">
                                <strong>${book.title}</strong> by ${book.author}
                                <span class="similarity">${Math.round(book.similarityScore * 100)}% match</span>
                            </a>
                            <p class="book-description">${book.description.substring(0, 100)}...</p>
                        </li>
                    `;
                });

                responseHtml += `</ul>`;
                addChatMessage(responseHtml, 'incoming', true);
            } else {
                addChatMessage("I couldn't find any books matching your description. Try being more specific!", 'incoming');
            }

        } catch (error) {
            typingIndicator.remove();
            addChatMessage("Sorry, I encountered an error. Please try again later.", 'incoming');
            console.error('Chatbot error:', error);
        }
    }

    // Helper function to add messages to chat
    function addChatMessage(message, type, isHtml = false) {
        const li = document.createElement('li');
        li.className = `chat-${type} chat`;

        if (isHtml) {
            li.innerHTML = message;
        } else {
            const p = document.createElement('p');
            p.textContent = message;
            li.appendChild(p);
        }

        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;
        return li;
    }

    // Event listeners
    sendBtn.addEventListener('click', sendMessage);
    textarea.addEventListener('keypress', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });
});