let pendingChanges = new Map();

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.update-field').forEach(field => {
        field.addEventListener('change', function() {
            handleFieldChange(this);
        });
    });
    
    // Clear changes after successful save
    const alertElement = document.querySelector('.alert-success');
    if (alertElement && alertElement.textContent.includes('saved successfully')) {
        pendingChanges.clear();
        document.querySelectorAll('.changed-row').forEach(row => {
            row.classList.remove('changed-row');
        });
        updateSaveButton();
        updateSaveIndicator();
    }
});

function handleFieldChange(field) {
    const paymentId = field.dataset.paymentId;
    const fieldName = field.dataset.field;
    const originalValue = field.dataset.original || '';
    const newValue = field.value;
    
    const changeKey = `${paymentId}-${fieldName}`;
    
    if (newValue !== originalValue) {
        pendingChanges.set(changeKey, {
            paymentId: paymentId,
            field: fieldName,
            value: newValue,
            originalValue: originalValue
        });
        
        const row = document.getElementById(`row-${paymentId}`);
        if (row) {
            row.classList.add('changed-row');
        }
    } else {
        pendingChanges.delete(changeKey);
        
        const hasOtherChanges = Array.from(pendingChanges.keys()).some(key => key.startsWith(`${paymentId}-`));
        if (!hasOtherChanges) {
            const row = document.getElementById(`row-${paymentId}`);
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

function viewReceipt(fileName) {
    const receiptImage = document.getElementById('receiptImage');
    const receiptError = document.getElementById('receiptError');
    const downloadLink = document.getElementById('downloadReceipt');
    
    const receiptUrl = '/uploads/receipts/' + fileName;
    
    receiptImage.src = receiptUrl;
    receiptImage.style.display = 'block';
    receiptError.style.display = 'none';
    downloadLink.href = receiptUrl;
    
    receiptImage.onerror = function() {
        receiptImage.style.display = 'none';
        receiptError.style.display = 'block';
    };
}

// Form submission handler
document.getElementById('bulkUpdateForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    if (pendingChanges.size === 0) {
        alert('No changes to save!');
        return;
    }
    
    const container = document.getElementById('changesContainer');
    container.innerHTML = '';
    
    // Update original values before submit
    pendingChanges.forEach((change, key) => {
        const field = document.querySelector(`[data-payment-id="${change.paymentId}"][data-field="${change.field}"]`);
        if (field) {
            field.dataset.original = change.value;
        }
    });
    
    let index = 0;
    pendingChanges.forEach((change, key) => {
        const paymentIdInput = document.createElement('input');
        paymentIdInput.type = 'hidden';
        paymentIdInput.name = `changes[${index}].paymentId`;
        paymentIdInput.value = change.paymentId;
        container.appendChild(paymentIdInput);
        
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
    
    this.submit();
});