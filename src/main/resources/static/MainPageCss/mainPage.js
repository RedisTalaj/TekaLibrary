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

    // Auto-scroll for newest books (first category only)
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

    // Manual scroll for other categories
    function setupHorizontalScroll(containerId, prevButtonId, nextButtonId) {
        const container = document.getElementById(containerId);
        const prevButton = document.getElementById(prevButtonId);
        const nextButton = document.getElementById(nextButtonId);

        console.log(`Container: ${containerId}`, container); // Debug
        console.log(`Prev Button: ${prevButtonId}`, prevButton); // Debug
        console.log(`Next Button: ${nextButtonId}`, nextButton); // Debug

        if (!container || !prevButton || !nextButton) {
            console.error("One or more elements are missing.");
            return;
        }

        const scrollAmount = 300;

        prevButton.addEventListener('click', () => {
            console.log("Prev button clicked"); // Debug
            container.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
        });

        nextButton.addEventListener('click', () => {
            console.log("Next button clicked"); // Debug
            container.scrollBy({ left: scrollAmount, behavior: 'smooth' });
        });
    }

    // Setup manual scroll for all other categories
    setupHorizontalScroll('newest-books-container', 'newest-prev', 'newest-next');
    setupHorizontalScroll('all-books-container', 'all-prev', 'all-next');
    setupHorizontalScroll('fiction1-books-container', 'fiction1-prev', 'fiction1-next');
    setupHorizontalScroll('horror-books-container', 'horror-prev', 'horror-next');
    setupHorizontalScroll('fiction-books-container', 'fiction-prev', 'fiction-next');
});

// Toggle favorite functionality
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