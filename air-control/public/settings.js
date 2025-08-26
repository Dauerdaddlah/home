const html = `<!-- New section for settings -->
        <div class="section">
            <h2>Settings</h2>
            <div class="settings-grid">
                <div class="input-group">
                    <label for="start1-input">Start 1:</label>
                    <input type="number" id="start1-input" min="0" max="100" value="20">
                </div>
                <div class="input-group">
                    <label for="end1-input">End 1:</label>
                    <input type="number" id="end1-input" min="0" max="100" value="15">
                </div>
                <div class="input-group">
                    <label for="start2-input">Start 2:</label>
                    <input type="number" id="start2-input" min="0" max="100" value="40">
                </div>
                <div class="input-group">
                    <label for="end2-input">End 2:</label>
                    <input type="number" id="end2-input" min="0" max="100" value="35">
                </div>
                <div class="input-group">
                    <label for="start3-input">Start 3:</label>
                    <input type="number" id="start3-input" min="0" max="100" value="60">
                </div>
                <div class="input-group">
                    <label for="end3-input">End 3:</label>
                    <input type="number" id="end3-input" min="0" max="100" value="55">
                </div>
            </div>
            <button id="send-settings-button" style="margin-top: 25px;">Send Settings</button>
        </div>`;

const appContainer = document.getElementById('app-container');

const template = document.createElement('template');
template.innerHTML = html.trim();

appContainer.appendChild(template.content);
