/**
 * 
 * @param {SubmitEvent} event 
 */
async function handleSubmitWorkout(event) {
    event.preventDefault();

    const workoutForm = document.getElementById('workoutForm');
    const formData = new FormData(workoutForm);

    const date = `${formData.get('date')}:00Z` || new Date(Date.now()).toISOString();
    const caloriesBurned = formData.get('caloriesBurned');
    const durationMinutes = formData.get('durationMinutes');

    const workout = {
        date: date,
        caloriesBurned: parseInt(caloriesBurned),
        durationMinutes: parseInt(durationMinutes)
    };

    const headers = new Headers();
    headers.append('Content-Type', 'application/json');

    try {
        const response = await fetch('http://localhost:8080/api/v1/workouts', {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(workout)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        workoutForm.reset()

        const data = await response.json();
        alert(data);
    } catch (error) {
        alert(error);
    }
}

/**
 * 
 * @param {SubmitEvent} event 
 */
async function handleSubmitMeal(event) {
    event.preventDefault();

    const form = document.getElementById('mealForm');
    const formData = new FormData(form);

    // Prepare the date field
    const date = `${formData.get('date')}:00Z`;

    // Prepare the foods array
    const foods = [];
    const foodItems = document.querySelectorAll("#foods li");
    foodItems.forEach(item => {
        const foodName = item.querySelector("input[name^='foods'][name$='[name]']").value;
        const calories = item.querySelector("input[name^='foods'][name$='[calories]']").value;
        foods.push({ name: foodName, calories: parseInt(calories) });
    });

    const result = { date: date, foods: foods };
    const body = JSON.stringify(result);
    const headers = new Headers({ 'Content-Type': 'application/json' });

    try {
        const response = await fetch('http://localhost:8080/api/v1/meals', {
            method: 'POST',
            headers: headers,
            body: body
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        alert(response)

        // Reset the form after successful submission
        form.reset();
        document.getElementById('foods').innerHTML = ''; // Clear all food items
    } catch (error) {
        console.error("Failed to submit meal:", error);
        alert(error)
    }
}

function addFood() {
    const ul = document.getElementById('foods');
    if (ul.childNodes.length >= 10) return; // Limit to 10 items

    const li = document.createElement('li');
    li.classList.add('flex', 'items-center', 'space-x-4', 'bg-white', 'p-4', 'rounded', 'shadow', 'mb-4');

    const name = document.createElement('input');
    name.placeholder = 'Name';
    name.maxLength = 30;
    name.required = true;
    name.name = 'foods[][name]';  // Adjusted name for food name input
    name.classList.add('w-full', 'px-4', 'py-2', 'border', 'rounded', 'focus:ring', 'focus:ring-blue-300', 'text-gray-700', 'focus:outline-none');

    const calorie = document.createElement('input');
    calorie.name = 'foods[][calories]';  // Adjusted name for calories input
    calorie.required = true;
    calorie.type = 'number';
    calorie.min = 1;
    calorie.max = 10000;
    calorie.placeholder = 'Calories';
    calorie.classList.add('w-32', 'px-4', 'py-2', 'border', 'rounded', 'focus:ring', 'focus:ring-blue-300', 'text-gray-700', 'focus:outline-none');

    const remove = document.createElement('button');
    remove.onclick = () => ul.removeChild(li);
    remove.innerText = 'Remove';
    remove.type = 'button';
    remove.classList.add('bg-red-500', 'text-white', 'px-4', 'py-2', 'rounded', 'hover:bg-red-600', 'focus:outline-none');

    li.append(name, calorie, remove);

    ul.append(li);
}

/**
 * 
 * @param {SubmitEvent} event 
 * @param {string} baseUrl 
 */
async function handleSubmitAdvice(event, baseUrl) {
    event.preventDefault()

    // Get chat log
    const chatLog = document.getElementById('chatLog')

    // Get user message
    const chatMessage = document.getElementById('chatMessage')

    // Create a log from the message
    const log = createChatLogEntry('User', chatMessage.value)

    // Add the log to the chat log
    chatLog.appendChild(log)

    // Create uri
    const uri = createAdviceUrl(baseUrl, chatMessage.value)

    // Clear the user's input message
    chatMessage.value = ''

    try {
        const response = await fetchStreamWithRetry(uri);
        const reader = response.body.getReader();
        let botLog = createChatLogEntry('Bot', '');
        chatLog.appendChild(botLog)
        let contentElement = botLog.querySelector('.message-content');
        await processStream(reader, contentElement, chatLog);
    } catch (error) {
        console.error('Error fetching chatbot response:', error);
        const errorLog = createChatLogEntry('System', 'An error occurred while fetching the response. Please try again.');
        chatLog.appendChild(errorLog)
    }

}

/**
 * 
 * @param {string} sender The chat sender
 * @param {string} message The message
 */
function createChatLogEntry(sender, message) {
    const entry = document.createElement('div');
    entry.classList.add('mb-4', 'flex', 'flex-col', 'items-start');

    // Apply styling for sender and message
    entry.innerHTML = `
        <div class="text-sm font-semibold ${
            sender === 'User' ? 'text-blue-500' : 'text-green-500'
        }">${sender}:</div>
        <div class="message-content p-3 rounded-lg max-w-lg ${
            sender === 'User'
                ? 'bg-blue-100 text-gray-800 self-end'
                : 'bg-green-100 text-gray-800 self-start'
        }">
            ${message}
        </div>
    `;
    return entry;
}

/**
 * URLEncodes the given message and concatenates
 * it to the given baseUri.
 * @param {string} baseUri
 * @param {string} message 
 */
function createAdviceUrl(baseUrl, message) {
    return baseUrl + "?message=" + encodeURIComponent(message)
}

/**
 * 
 * @param {ReadableStreamDefaultReader<Uint8Array>} reader 
 * @param {HTMLDivElement} contentElement The stream's output element
 * @param {HTMLDivElement} chatLog The output container for new messages
 */
async function processStream(reader, contentElement, chatLog) {
    const decoder = new TextDecoder("utf-8");
    try {
        while (true) {
            const { done, value } = await reader.read();
            if (done) break;
            contentElement.innerHTML += decoder.decode(value, { stream: true });
            chatLog.scrollTop = chatLog.scrollHeight;
        }
    } catch (error) {
        console.error('Error processing stream:', error);
        contentElement.innerHTML += '<br>[Error: Stream interrupted. Please try again.]';
    }
}

/**
 * 
 * @param {string} url The url to fetch
 * @param {number} retries fetch retries
 * @returns 
 */
async function fetchStreamWithRetry(url, retries = 3) {
    for (let i = 0; i < retries; i++) {
        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            return response;
        } catch (e) {
            console.error(`Attempt ${i + 1} failed: ${e.message}`);
            if (i === retries - 1) throw e;
            await new Promise(resolve => setTimeout(resolve, 1000)); // Wait 1 second before retrying
        }
    }
}

function handleMealReset() {
    document.getElementById('foods').innerHTML = ''
}
