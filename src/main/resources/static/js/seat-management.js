/**
 * Seat Management Page JavaScript
 * Handles bulk updates and change tracking
 */

let pendingChanges = new Map();

document.addEventListener('DOMContentLoaded', function() {
    // Add change listeners to all update fields
    document.querySelectorAll('.update-field').forEach(field => {
        field.addEventListener('change', function() {
            handleFieldChange(this);
        });
    });
});

function handleFieldChange(field) {
    const appId = field.dataset.appId;
    const fieldName = field.dataset.field;
    const originalValue = field.dataset.original || '';
    const newValue = field.value;
    
    // Create unique key for this field
    const changeKey = `${appId}-${fieldName}`;
    
    if (newValue !== originalValue) {
        // Mark as changed
        pendingChanges.set(changeKey, {
            appId: appId,
            field: fieldName,
            value: newValue,
            originalValue: originalValue
        });
        
        // Highlight the row
        const row = document.getElementById(`row-${appId}`);
        if (row) {
            row.classList.add('changed-row');
        }
    } else {
        // Remove from pending changes if value matches original
        pendingChanges.delete(changeKey);
        
        // Check if row has any other changes
        const hasOtherChanges = Array.from(pendingChanges.keys()).some(key => key.startsWith(`${appId}-`));
        if (!hasOtherChanges) {
            const row = document.getElementById(`row-${appId}`);
            if (row) {
                row.classList.remove('changed-row');
            }
        }
    }
    
    updateSaveButton();
    updateSaveIndicator();
}

function updateSaveButton() {
    const saveBtn = document.getElementById('saveAllBtn');
    const saveCount = document.getElementById('saveCount');
    const changeCount = pendingChanges.size;
    
    if (changeCount > 0) {
        saveBtn.style.display = 'block';
        saveCount.textContent = changeCount;
    } else {
        saveBtn.style.display = 'none';
    }
}

function updateSaveIndicator() {
    const indicator = document.getElementById('saveIndicator');
    const changeCountSpan = document.getElementById('changeCount');
    const changeCount = pendingChanges.size;
    
    if (changeCount > 0) {
        indicator.style.display = 'block';
        changeCountSpan.textContent = changeCount;
    } else {
        indicator.style.display = 'none';
    }
}

function hideSaveIndicator() {
    document.getElementById('saveIndicator').style.display = 'none';
}

// Handle form submission
document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('bulkUpdateForm').addEventListener('submit', function(e) {
        e.preventDefault();
        
        if (pendingChanges.size === 0) {
            alert('No changes to save!');
            return;
        }
        
        // Clear existing hidden inputs
        const container = document.getElementById('changesContainer');
        container.innerHTML = '';
        
        // Add hidden inputs for each change
        let index = 0;
        pendingChanges.forEach((change, key) => {
            const appIdInput = document.createElement('input');
            appIdInput.type = 'hidden';
            appIdInput.name = `changes[${index}].appId`;
            appIdInput.value = change.appId;
            container.appendChild(appIdInput);
            
            const fieldInput = document.createElement('input');
            fieldInput.type = 'hidden';
            fieldInput.name = `changes[${index}].field`;
            fieldInput.value = change.field;
            container.appendChild(fieldInput);
            
            const valueInput = document.createElement('input');
            valueInput.type = 'hidden';
            valueInput.name = `changes[${index}].value`;
            valueInput.value = change.value;
            container.appendChild(valueInput);
            
            index++;
        });
        
        // Submit the form
        this.submit();
    });
});