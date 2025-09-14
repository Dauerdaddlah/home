const systemResponseDisplay = document.getElementById('system-response');

// Define the possible states of the ventilation system.
const systemStates = ['Default', 'Off', 'Low', 'Medium', 'High'];

// Get references to all the necessary HTML elements.
const currentStateDisplay = document.getElementById('current-state-display');
const stateSlider = document.getElementById('state-slider');
const sliderValueDisplay = document.getElementById('slider-value');
const intervalInput = document.getElementById('interval-input');
const sendCommandButton = document.getElementById('send-command-button');


// New references for the status indicators
const tempDisplay = document.getElementById('temp-display');
const humidityDisplay = document.getElementById('humidity-display');
const filterDisplay = document.getElementById('filter-display');

// New references for the manual level control
const levelInput = document.getElementById('level-input');
const sendLevelButton = document.getElementById('send-level-button');

// References for the collapsible section
const manualLevelToggle = document.getElementById('manual-level-toggle');
const collapsibleContent = document.querySelector('.collapsible-content');

// New references for the settings section
const start1Input = document.getElementById('start1-input');
const start2Input = document.getElementById('start2-input');
const start3Input = document.getElementById('start3-input');
const end1Input = document.getElementById('end1-input');
const end2Input = document.getElementById('end2-input');
const end3Input = document.getElementById('end3-input');
const sendSettingsButton = document.getElementById('send-settings-button');

// Function to update the text display based on the slider's value.
const updateSliderDisplay = () => {
    const stateIndex = parseInt(stateSlider.value);
    sliderValueDisplay.textContent = systemStates[stateIndex];
};

// Function to display a message to the user in the status area.
const displayResponse = (message) => {
    systemResponseDisplay.textContent = `[${new Date().toLocaleTimeString()}] ${message}`;
};

// Simulate fetching the system's current state from a server.
const fetchSystemState = async () => {
    displayResponse('Fetching current system state...');
    try {
        const response = await fetch('/state', { method: 'GET' });
        /*
        // Mock a GET request with a 1.5-second delay.
        const response = await new Promise(resolve => {
            setTimeout(() => {
                // Simulate a successful response with a random state and other data
                const mockState = Math.floor(Math.random() * systemStates.length);
                const mockTemp = (Math.random() * (25 - 18) + 18).toFixed(1); // Temp between 18 and 25
                const mockHumidity = Math.floor(Math.random() * (60 - 40) + 40); // Humidity between 40 and 60
                const mockFilter = Math.floor(Math.random() * 100); // Filter life from 0 to 100
                resolve({
                    ok: true,
                    json: () => Promise.resolve({ 
                        state: mockState, 
                        temperature: mockTemp, 
                        humidity: mockHumidity, 
                        filterLife: mockFilter, 
                        message: 'Initial state retrieved' 
                    })
                });
            }, 1500);
        });
        */

        if (!response.ok) {
            throw new Error('Failed to fetch state');
        }

        const data = await response.json();

        // Update the displays with the received data
        currentStateDisplay.textContent = systemStates[data.level];
        tempDisplay.textContent = data.temperature;
        humidityDisplay.textContent = data.humidity;
        
        stateSlider.value = data.level; // Set the slider to the fetched state
        updateSliderDisplay();
        displayResponse(`Received system state: "${systemStates[data.level]}", Temp: ${data.temperature}°C, Humid: ${data.humidity}%`);

    } catch (error) {
        displayResponse(`Error fetching state: ${error.message}`);
        currentStateDisplay.textContent = 'Error';
    }
};

// Simulate sending a new command to the server via a POST request.
const sendSystemCommand = async () => {
    const selectedStateIndex = parseInt(stateSlider.value);
    const selectedState = systemStates[selectedStateIndex];
    const selectedInterval = parseInt(intervalInput.value);

    // Basic validation for the interval
    if (isNaN(selectedInterval) || selectedInterval <= 0) {
        displayResponse('Error: Please enter a valid interval in minutes (a positive number).');
        return;
    }

    // Prepare the data to be sent in the request body.
    const requestBody = {
        state: selectedState,
        interval: selectedInterval
    };

    displayResponse(`Sending command to set state to "${selectedState}" for ${selectedInterval} minutes...`);

    try {
        // Mock a POST request with a 2-second delay.
        const response = await new Promise(resolve => {
            setTimeout(() => {
                // Simulate a successful response
                resolve({
                    ok: true,
                    json: () => Promise.resolve({
                        success: true,
                        new_state: selectedState,
                        new_interval: selectedInterval,
                        log: `Command received: set to ${selectedState} for ${selectedInterval} min`
                    })
                });
            }, 2000);
        });

        if (!response.ok) {
            throw new Error('Server responded with an error');
        }

        const data = await response.json();
        displayResponse(`Server response: "${data.log}"`);
        
        // Update the displayed current state with the new state from the server.
        currentStateDisplay.textContent = data.new_state;

    } catch (error) {
        displayResponse(`Error sending command: ${error.message}`);
    }
};

// Function to handle sending a manual level.
const sendManualLevel = async () => {
    const selectedLevel = parseInt(levelInput.value);

    // Basic validation for the level
    if (isNaN(selectedLevel) || selectedLevel < 1 || selectedLevel > 100) {
        displayResponse('Error: Please enter a valid level between 1 and 100.');
        return;
    }

    // Prepare the data to be sent.
    const requestBody = {
        level: selectedLevel
    };

    displayResponse(`Sending manual level command: ${selectedLevel}...`);

    try {
        // Mock a POST request with a 2-second delay.
        const response = await new Promise(resolve => {
            setTimeout(() => {
                // Simulate a successful response
                resolve({
                    ok: true,
                    json: () => Promise.resolve({
                        success: true,
                        new_level: selectedLevel,
                        log: `Manual level command received: set to ${selectedLevel}`
                    })
                });
            }, 2000);
        });

        if (!response.ok) {
            throw new Error('Server responded with an error');
        }

        const data = await response.json();
        displayResponse(`Server response: "${data.log}"`);
        
        // For a manual level, you might update a different display or log the change.
        // Here, we'll just log the success.
    } catch (error) {
        displayResponse(`Error sending level command: ${error.message}`);
    }
};

// Function to handle sending settings
const sendSettingsCommand = async () => {
    const start1 = parseInt(start1Input.value);
    const start2 = parseInt(start2Input.value);
    const start3 = parseInt(start3Input.value);
    const end1 = parseInt(end1Input.value);
    const end2 = parseInt(end2Input.value);
    const end3 = parseInt(end3Input.value);

    // Validation for settings
    if (isNaN(start1) || isNaN(start2) || isNaN(start3) || isNaN(end1) || isNaN(end2) || isNaN(end3)) {
        displayResponse('Error: All settings inputs must be numbers.');
        return;
    }
    if (start1 < 0 || start1 > 100 || start2 < 0 || start2 > 100 || start3 < 0 || start3 > 100 || end1 < 0 || end1 > 100 || end2 < 0 || end2 > 100 || end3 < 0 || end3 > 100) {
        displayResponse('Error: All settings values must be between 0 and 100.');
        return;
    }
    if (start1 > start2 || start2 > start3) {
        displayResponse('Error: start1 <= start2 <= start3 must be true.');
        return;
    }
    if (end1 > start1 || end2 > start2 || end3 > start3) {
        displayResponse('Error: end values must be less than or equal to their corresponding start values.');
        return;
    }

    // Prepare the data to be sent.
    const requestBody = {
        start1, start2, start3, end1, end2, end3
    };

    displayResponse(`Sending settings command...`);

    try {
        // Mock a POST request with a 2-second delay.
        const response = await new Promise(resolve => {
            setTimeout(() => {
                // Simulate a successful response
                resolve({
                    ok: true,
                    json: () => Promise.resolve({
                        success: true,
                        log: `Settings received: ${JSON.stringify(requestBody)}`
                    })
                });
            }, 2000);
        });

        if (!response.ok) {
            throw new Error('Server responded with an error');
        }

        const data = await response.json();
        displayResponse(`Server response: "${data.log}"`);
        
    } catch (error) {
        displayResponse(`Error sending settings: ${error.message}`);
    }
};


// Add event listeners for user interaction.
stateSlider.addEventListener('input', updateSliderDisplay);
sendCommandButton.addEventListener('click', sendSystemCommand);
sendLevelButton.addEventListener('click', sendManualLevel); // Event listener for the new button
sendSettingsButton.addEventListener('click', sendSettingsCommand);

// Event listener for the collapsible section
manualLevelToggle.addEventListener('click', () => {
    manualLevelToggle.classList.toggle('active');
    if (collapsibleContent.style.maxHeight) {
        collapsibleContent.style.maxHeight = null;
    } else {
        collapsibleContent.style.maxHeight = collapsibleContent.scrollHeight + "px";
    }
});

// Fetch the initial state when the page loads.
window.onload = fetchSystemState;
