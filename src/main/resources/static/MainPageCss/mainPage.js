document.addEventListener('DOMContentLoaded', function () {
    // Dropdown functionality
    const dropdownTrigger = document.querySelector('.dropdown-trigger');
    const dropdownMenu = document.querySelector('.dropdown');

    if (dropdownTrigger && dropdownMenu) {
        dropdownTrigger.addEventListener('click', function (e) {
            e.preventDefault(); // Prevent the link from navigating
            dropdownMenu.style.display = dropdownMenu.style.display === 'block' ? 'none' : 'block';
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', function (e) {
            if (!e.target.matches('.dropdown-trigger') && !e.target.closest('.dropdown')) {
                dropdownMenu.style.display = 'none';
            }
        });
    }

    // Auto-scroll for newest books
    const newestContainer = document.getElementById('newest-books-container');
    let scrollInterval;

    if (newestContainer) {
        function startAutoScroll() {
            scrollInterval = setInterval(() => {
                newestContainer.scrollBy({ left: 200, behavior: 'smooth' });
                if (newestContainer.scrollLeft + newestContainer.clientWidth >= newestContainer.scrollWidth) {
                    newestContainer.scrollTo({ left: 0, behavior: 'smooth' });
                }
            }, 3000);
        }

        function stopAutoScroll() {
            clearInterval(scrollInterval);
        }

        newestContainer.addEventListener('mouseenter', stopAutoScroll);
        newestContainer.addEventListener('mouseleave', startAutoScroll);

        startAutoScroll();
    }

    // Horizontal scroll for all sections
    function setupHorizontalScroll(containerId, prevButtonId, nextButtonId) {
        const container = document.getElementById(containerId);
        const prevButton = document.getElementById(prevButtonId);
        const nextButton = document.getElementById(nextButtonId);

        if (!container || !prevButton || !nextButton) return;

        const scrollAmount = 300;

        prevButton.addEventListener('click', () => {
            container.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
        });

        nextButton.addEventListener('click', () => {
            container.scrollBy({ left: scrollAmount, behavior: 'smooth' });
        });
    }

    setupHorizontalScroll('newest-books-container', 'newest-prev', 'newest-next');
    setupHorizontalScroll('fiction-books-container', 'fiction-prev', 'fiction-next');
});

function addToFavorites(element) {
    const bookId = element.getAttribute('data-book-id'); // Get the book ID from the data attribute
    const userId = element.getAttribute('data-user-id');

    if (!bookId || !userId) {
        console.error("Book ID or User ID is missing.");
        return;
    }

    // Send an AJAX request to add the book to favorites
    fetch(`/users/favourite/add/${bookId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content') // Add CSRF token
        },
        body: JSON.stringify({ userId: userId })
    })
        .then(response => {
            if (response.ok) {
                // Update the UI to reflect the book has been added to favorites
                element.classList.remove('fa-regular'); // Remove the regular star
                element.classList.add('fa-solid'); // Add the solid star
                alert("Book added to favorites!");
            } else {
                // Handle errors (e.g., display an error message)
                console.error("Error adding to favorites:", response.status);
                alert("Error adding to faavorites. Please try again.");
            }
        })
        .catch(error => {
            console.error("Error adding to favorites:", error);
            alert("An error occurred. Please try again later.");
        });
}
function toggleFavorite(element) {
    console.log("toggleFavorite called"); // Debug statement
    if (!element) {
        console.error("Element is null");
        return;
    }

    const bookId = element.getAttribute('data-book-id');
    const userId = element.getAttribute('data-user-id');

    console.log("Book ID:", bookId); // Debug statement
    console.log("User ID:", userId); // Debug statement

    if (!bookId || !userId) {
        console.error("Book ID or User ID is missing.");
        return;
    }

    // Send an AJAX request to toggle the favorite status
    fetch(`/users/favourite/toggle/${bookId}?userId=${userId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                return response.text(); // Get the response message
            } else {
                throw new Error("Error toggling favorite");
            }
        })
        .then(message => {
            // Toggle the star icon
            if (element.classList.contains('fa-solid')) {
                element.classList.remove('fa-solid');
                element.classList.add('fa-regular');
            } else {
                element.classList.remove('fa-regular');
                element.classList.add('fa-solid');
            }
            alert(message); // Show success message
        })
        .catch(error => {
            console.error("Error toggling favorite:", error);
            alert("An error occurred. Please try again later.");
        });
}