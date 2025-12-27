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

// references for the cleaning section
const cleaningToggle = document.getElementById('cleaning-toggle');
const cleaningContent = document.querySelector('.cleaning-content');
const cleaningList = document.getElementById('cleaning-list');
const sendCleaningButton = document.getElementById('send-cleaning-button');
const cleaningDateInput = document.getElementById('cleaning-date');
const filterExtraOption = document.getElementById('filter-extra-option');
const filterInterval2Check = document.getElementById('filter-interval-2-check');

let selectedCleaningIds = new Set();

const fetchCleaningData = async () => {
    try {
        const response = await fetch('/v1/cleaning');
        if (!response.ok) throw new Error("Fehler beim Laden");
        const cleanings = await response.json();
        
        cleaningList.innerHTML = '';
        cleanings.forEach((item, index) => {
            const div = document.createElement('div');
            div.className = 'cleaning-item';

            // 1. Status bestimmen
            let statusClass = '';
            if (!item.lastCleaning || (index == 0 && !item.lastReplacement)) {
                statusClass = 'status-critical'; // Rot, wenn kein Zeitpunkt vorhanden
            } else {
                const lastDate = new Date(item.lastCleaning);
                const today = new Date();
                
                // Berechnung: Zeitpunkt + Intervall in Monaten
                const dueDateMin = new Date(lastDate);
                dueDateMin.setMonth(dueDateMin.getMonth() + item.intervalMin);
                const dueDateMax = new Date(lastDate);
                dueDateMax.setMonth(dueDateMax.getMonth() + item.intervalMax);
                
                // Schwellenwerte berechnen
                const criticalDate = new Date(dueDateMax);
                criticalDate.setDate(criticalDate.getDate() + 15); // + 15 Tage
                
                const warningDate = new Date(dueDateMin);
                warningDate.setDate(warningDate.getDate() - 15); // - 15 Tage
                
                if(index == 0)
                {
                    const lastDate2 = new Date(item.lastReplacement);
                    
                    // Berechnung: Zeitpunkt + Intervall in Monaten
                    const dueDateMin2 = new Date(lastDate2);
                    dueDateMin2.setMonth(dueDateMin2.getMonth() + (item.intervalMin * item.replacementInterval));
                    const dueDateMax2 = new Date(lastDate2);
                    dueDateMax2.setMonth(dueDateMax2.getMonth() + (item.intervalMax * item.replacementInterval));
                    
                    // Schwellenwerte berechnen
                    const criticalDate2 = new Date(dueDateMax2);
                    criticalDate2.setDate(criticalDate2.getDate() + 15); // + 15 Tage
                    
                    const warningDate2 = new Date(dueDateMin2);
                    warningDate2.setDate(warningDate2.getDate() - 15); // - 15 Tage
                    
                    if (today > criticalDate || today > criticalDate2) {
                        statusClass = 'status-critical'; // Rot: Überfällig + 15 Tage
                    } else if (today > warningDate || today > warningDate2) {
                        statusClass = 'status-warning'; // Gelb: Bereich +/- 15 Tage um das Intervall
                    }
                }
                else
                {
                    if (today > criticalDate) {
                        statusClass = 'status-critical'; // Rot: Überfällig + 15 Tage
                    } else if (today > warningDate) {
                        statusClass = 'status-warning'; // Gelb: Bereich +/- 15 Tage um das Intervall
                    }
                }
            }

            if (statusClass) div.classList.add(statusClass);
            
            // Formatierung: Letztes Datum anzeigen
            const lastDateStr = item.lastCleaning ? new Date(item.lastCleaning).toLocaleDateString('de-DE') : 'Nie';
            const lastDateStr2 = item.lastReplacement ? new Date(item.lastReplacement).toLocaleDateString('de-DE') : 'Nie';
            
            let intervallString = `${item.intervalMin}`;
            let intervallString2 = `${item.intervalMin * item.replacementInterval}`;

            if(item.intervalMin != item.intervalMax)
            {
                intervallString += ` - ${item.intervalMax}`;
                intervallString2 += ` - ${item.intervalMax * item.replacementInterval}`;
            }

            div.innerHTML = `
                <input type="checkbox" class="cleaning-checkbox" id="check-${item.number}">
                <div class="cleaning-info">
                    <strong>${item.name}</strong>
                    <span>Alle  ${intervallString} Monate Zuletzt: ${lastDateStr}</span>
                    ${index === 0 ?
                         `<div class="cleaning-extra-row">
                            <input type="checkbox" id="filter-interval-2-check" class="extra-cb">
                            <label for="filter-interval-2-check">Filterwechsel alle ${intervallString2} Monate Zuletzt: ${lastDateStr2}</label>
                        </div>`
                                : ''}
                </div>
            `;

            div.onclick = (e) => {
                // Verhindert Doppelklick, wenn man direkt auf die Checkbox klickt
                if (e.target.type !== 'checkbox') {
                    const cb = div.querySelector('.cleaning-checkbox');
                    cb.checked = !cb.checked;
                }
                
                const isChecked = div.querySelector('.cleaning-checkbox').checked;
                if (isChecked) selectedCleaningIds.add(item.number);
                else selectedCleaningIds.delete(item.number);
                
                div.classList.toggle('selected', isChecked);
            };
            cleaningList.appendChild(div);
        });
    } catch (e) {
        console.error(e);
    }
};

sendCleaningButton.onclick = async () => {
    if (selectedCleaningIds.size === 0) {
        displayResponse("Fehler: Keine Reinigung ausgewählt.");
        return;
    }

    const cleaningDate = cleaningDateInput.value;
    const extraCb = document.getElementById('filter-interval-2-check');
    const resetFilter2 = extraCb ? extraCb.checked : false;

    // Wir erstellen ein Array von Promises (eine pro ausgewählter ID)
    const requests = Array.from(selectedCleaningIds).map(id => {
        const payload = {
            ldt: cleaningDate,
            // Wir senden resetFilterInterval2 nur mit, wenn es die Filterpflege (ID 0 oder 1, je nach Backend) betrifft
            // Oder wir senden es bei jeder Anfrage mit, falls das Backend es ignoriert
            replaced: (id === 0 || id === 1) ? resetFilter2 : false 
        };

        return fetch(`/v1/cleaning/${id}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });
    });

    displayResponse(`Sende ${requests.length} Anfragen...`);

    try {
        // Alle Anfragen parallel ausführen
        const responses = await Promise.all(requests);
        
        // Prüfen, ob alle Anfragen erfolgreich (Status 200-299) waren
        const allOk = responses.every(res => res.ok);

        if (allOk) {
            displayResponse("Alle Reinigungen erfolgreich gespeichert.");
            selectedCleaningIds.clear();
            // Liste aktualisieren, um die neuen "lastDone" Daten zu sehen
            fetchCleaningData(); 
        } else {
            displayResponse("Einige Anfragen sind fehlgeschlagen.");
        }
    } catch (e) {
        displayResponse(`Netzwerkfehler: ${e.message}`);
        console.error(e);
    }
};

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

    fetchCleaningData();
    try {
        const response = await fetch('/v1/state', { method: 'GET' });

        if (!response.ok) {
            throw new Error('Failed to fetch state');
        }

        const data = await response.json();

        // Update the displays with the received data
        currentStateDisplay.textContent = systemStates[data.level];

        let tempValue = parseFloat(data.temperature);
        tempDisplay.textContent = tempValue == -1 ? '--' : tempValue.toFixed(1);

        if(tempValue != -1)
        {
            tempDisplay.textContent = tempValue.toFixed(1);

            // blau: 240, grün 120, gelb: 60, rot: 0
            // temp: 20 -> grün, 25 -> gelb, 30 -> rot, 17 -> blau

            let tempHue = 0;
            if(tempValue < 17)
            {
                tempHue = 240;
            }
            else if(tempValue < 20)
            {
                tempHue = 120 + ((20 - tempValue) / 3) * 120 // bis zu 17 Grad
            }
            else if(tempValue < 25)
            {
                tempHue = 60 + ((25 - tempValue) / 5) * 60; // bis zu 24 Grad
            }
            else if(tempValue < 30)
            {
                tempHue = 0 + ((30 - tempValue) / 5) * 60; // bis zu 29 Grad
            }

            const tempIcon = document.querySelector('.status-item:nth-child(1) svg');
            if (tempIcon) {
                tempIcon.style.fill = `hsl(${tempHue}, 80%, 45%)`;
                tempDisplay.style.color = `hsl(${tempHue}, 80%, 45%)`;
            }
        }

        let humidityValue = parseFloat(data.humidity);
        humidityDisplay.textContent = humidityValue == -1 ? '--' : humidityValue.toFixed(1);

        if(humidityValue != 1)
        {
            humidityValue -= 45;
            humidityValue = Math.max(0, humidityValue);
            humidityValue = Math.min(25, humidityValue);

            const humHue = (1 - (humidityValue / 25)) * 120

            const humidityIcon = document.querySelector('.status-item:nth-child(2) svg');
            if (humidityIcon) {
                humidityIcon.style.fill = `hsl(${humHue}, 80%, 45%)`;
                humidityDisplay.style.color = `hsl(${humHue}, 80%, 45%)`;
            }
        }

        let filterValue = parseFloat(data.filter);
        filterDisplay.textContent = filterValue.toFixed(1);

        if(filterValue != -1)
        {
            // Farbe berechnen: 120 ist Grün, 0 ist Rot im HSL Farbraum
            // > 80 ist grün, ab 40 ist rot
            // Wir interpolieren: 100% -> 120 (Grün), 0% -> 0 (Rot)
            filterValue = Math.min(80, filterValue);
            filterValue = Math.max(40, filterValue);

            filterValue -= 40;
            const hue = (filterValue * 3).toFixed(0);
            const filterIcon = document.querySelector('.status-item:last-child svg');
            if (filterIcon) {
                filterIcon.style.fill = `hsl(${hue}, 80%, 45%)`;
                filterDisplay.style.color = `hsl(${hue}, 80%, 45%)`;
            }
        }
        
        stateSlider.value = data.level; // Set the slider to the fetched state

        let interval = '--';

        if(!isNaN(data.interval))
        {
            interval = `${(data.interval / 60 / 1000) | 0}:${((data.interval / 1000) % 60) | 0 }`;
        }
        updateSliderDisplay();
        displayResponse(`Received system state: "${systemStates[data.level]}", Temp: ${data.temperature}°C, Humid: ${data.humidity}%, interval: ${interval}`);

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
        level: selectedStateIndex,
        interval: selectedInterval * 60 * 1000 // Convert minutes to milliseconds
    };

    displayResponse(`Sending command to set state to "${selectedState}" for ${selectedInterval} minutes...`);

    try {
        // Mock a POST request with a 2-second delay.
        const response = await fetch("/v1/state", { method: 'PUT', body: JSON.stringify(requestBody) });

        if (!response.ok) {
            throw new Error('Server responded with an error');
        }

        fetchSystemState();
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
// sendLevelButton.addEventListener('click', sendManualLevel); // Event listener for the new button
// sendSettingsButton.addEventListener('click', sendSettingsCommand);

// Event listener for the collapsible section
manualLevelToggle.addEventListener('click', () => {
    manualLevelToggle.classList.toggle('active');
    if (collapsibleContent.style.maxHeight) {
        collapsibleContent.style.maxHeight = null;
    } else {
        collapsibleContent.style.maxHeight = collapsibleContent.scrollHeight + "px";
    }
});

// Event listener for the collapsible section
cleaningToggle.addEventListener('click', () => {
    cleaningToggle.classList.toggle('active');
    if (cleaningContent.style.maxHeight) {
        cleaningContent.style.maxHeight = null;
    } else {
        cleaningContent.style.maxHeight = cleaningContent.scrollHeight + "px";
    }
});

// Fetch the initial state when the page loads.
window.onload = fetchSystemState;
