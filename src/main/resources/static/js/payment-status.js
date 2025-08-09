/**
 * Payment Status Page JavaScript
 * Handles fee calculation and payment processing
 */

const feeStructure = {
    btech: {
        'Computer Science': { tuition: 80000, development: 15000, lab: 10000, library: 3000, exam: 2000 },
        'Mechanical': { tuition: 75000, development: 15000, lab: 8000, library: 3000, exam: 2000 },
        'Electrical': { tuition: 75000, development: 15000, lab: 8000, library: 3000, exam: 2000 },
        'Civil': { tuition: 70000, development: 15000, lab: 5000, library: 3000, exam: 2000 },
        'Electronics': { tuition: 78000, development: 15000, lab: 9000, library: 3000, exam: 2000 }
    },
    bsc: {
        'Physics': { tuition: 45000, development: 10000, lab: 8000, library: 2000, exam: 1500 },
        'Chemistry': { tuition: 45000, development: 10000, lab: 10000, library: 2000, exam: 1500 },
        'Mathematics': { tuition: 40000, development: 10000, lab: 3000, library: 2000, exam: 1500 },
        'Biology': { tuition: 48000, development: 10000, lab: 12000, library: 2000, exam: 1500 }
    },
    bca: {
        'Computer Applications': { tuition: 55000, development: 12000, lab: 8000, library: 2500, exam: 1500 }
    },
    mtech: {
        'Computer Science': { tuition: 90000, development: 20000, lab: 15000, library: 4000, exam: 3000 },
        'Mechanical': { tuition: 85000, development: 20000, lab: 12000, library: 4000, exam: 3000 },
        'Electronics': { tuition: 88000, development: 20000, lab: 14000, library: 4000, exam: 3000 }
    }
};

const branches = {
    btech: ['Computer Science', 'Mechanical', 'Electrical', 'Civil', 'Electronics'],
    bsc: ['Physics', 'Chemistry', 'Mathematics', 'Biology'],
    bca: ['Computer Applications'],
    mtech: ['Computer Science', 'Mechanical', 'Electronics']
};

function updateBranches() {
    const courseSelect = document.getElementById('courseSelect');
    const branchSelect = document.getElementById('branchSelect');
    const selectedCourse = courseSelect.value;

    branchSelect.innerHTML = '<option value="">Choose Branch</option>';

    if (selectedCourse && branches[selectedCourse]) {
        branches[selectedCourse].forEach(branch => {
            const option = document.createElement('option');
            option.value = branch;
            option.textContent = branch;
            branchSelect.appendChild(option);
        });
    }

    document.getElementById('feeBreakdown').style.display = 'none';
}

function calculateFee() {
    const courseSelect = document.getElementById('courseSelect');
    const branchSelect = document.getElementById('branchSelect');
    const selectedCourse = courseSelect.value;
    const selectedBranch = branchSelect.value;

    if (selectedCourse && selectedBranch && feeStructure[selectedCourse] && feeStructure[selectedCourse][selectedBranch]) {
        const fees = feeStructure[selectedCourse][selectedBranch];
        const total = fees.tuition + fees.development + fees.lab + fees.library + fees.exam;

        document.getElementById('tuitionFee').textContent = '₹' + fees.tuition.toLocaleString();
        document.getElementById('developmentFee').textContent = '₹' + fees.development.toLocaleString();
        document.getElementById('labFee').textContent = '₹' + fees.lab.toLocaleString();
        document.getElementById('libraryFee').textContent = '₹' + fees.library.toLocaleString();
        document.getElementById('examFee').textContent = '₹' + fees.exam.toLocaleString();
        document.getElementById('totalAmount').textContent = '₹' + total.toLocaleString();
        document.getElementById('feeBreakdown').style.display = 'block';
    }
}

function handlePayNowClick() {
    document.getElementById('paymentOptionsSection').style.display = 'block';
    document.getElementById('paymentOptionsSection').scrollIntoView({ behavior: 'smooth' });
}

function selectPaymentMethod(method) {
    document.querySelectorAll('.payment-option').forEach(option => {
        option.classList.remove('border-primary', 'bg-light');
    });

    event.currentTarget.classList.add('border-primary', 'bg-light');

    const courseSelect = document.getElementById('courseSelect');
    const branchSelect = document.getElementById('branchSelect');

    let courseInfo = 'Manual Selection';
    if (courseSelect.selectedIndex > 0 && branchSelect.value) {
        courseInfo = courseSelect.options[courseSelect.selectedIndex].text + ' - ' + branchSelect.value;
    }

    document.getElementById('courseInfo').value = courseInfo;

    let methodText = '';
    switch(method) {
        case 'upi': methodText = 'UPI Payment'; break;
        case 'bank': methodText = 'Bank Transfer'; break;
        case 'receipt': methodText = 'Upload Receipt'; break;
    }
    document.getElementById('paymentMethodDisplay').value = methodText;

    const totalFeeText = document.getElementById('totalFeeAmount').textContent;
    const amount = totalFeeText.replace('₹', '').replace(/,/g, '');
    document.getElementById('amountPaid').value = amount;
    document.getElementById('amountPaid').readOnly = true;

    document.getElementById('paymentFormSection').style.display = 'block';
    document.getElementById('paymentFormSection').scrollIntoView({ behavior: 'smooth' });
}

function resetPaymentForm() {
    document.getElementById('paymentForm').reset();
    document.getElementById('paymentOptionsSection').style.display = 'none';
    document.getElementById('paymentFormSection').style.display = 'none';

    document.querySelectorAll('.payment-option').forEach(option => {
        option.classList.remove('border-primary', 'bg-light');
    });
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

document.addEventListener('DOMContentLoaded', function() {
    // Get application data from the page elements
    const courseElement = document.querySelector('.badge.bg-info');
    const branchElement = document.querySelector('.badge.bg-success');
    
    const appCourse = courseElement ? courseElement.textContent.trim() : '';
    const appAllocatedBranch = branchElement ? branchElement.textContent.trim() : '';

    const totalFeeElement = document.getElementById('totalFeeAmount');
    const payNowBtn = document.getElementById('payNowBtn');
    const statusMessage = document.getElementById('feeStatusMessage');

    const hasRequiredData = appCourse && appCourse !== '' && appAllocatedBranch && appAllocatedBranch !== '';
    
    if (hasRequiredData) {
        const courseMapping = {
            'BTECH': 'btech',
            'B.TECH': 'btech',
            'BSC': 'bsc',
            'B.SC': 'bsc', 
            'BCA': 'bca',
            'MTECH': 'mtech',
            'M.TECH': 'mtech'
        };

        const branchMapping = {
            'CSE': 'Computer Science',
            'ECE': 'Electronics', 
            'MECHANICAL': 'Mechanical',
            'CIVIL': 'Civil',
            'Computer Science': 'Computer Science',
            'Electronics': 'Electronics',
            'Mechanical': 'Mechanical', 
            'Civil': 'Civil',
            'Physics': 'Physics',
            'Chemistry': 'Chemistry',
            'Mathematics': 'Mathematics',
            'Biology': 'Biology',
            'Computer Applications': 'Computer Applications'
        };

        const courseKey = courseMapping[appCourse.toUpperCase()] || appCourse.toLowerCase();
        const branchKey = branchMapping[appAllocatedBranch] || appAllocatedBranch;

        if (courseKey && feeStructure[courseKey] && feeStructure[courseKey][branchKey]) {
            const fees = feeStructure[courseKey][branchKey];
            const totalAmount = fees.tuition + fees.development + fees.lab + fees.library + fees.exam;

            totalFeeElement.textContent = '₹' + totalAmount.toLocaleString();
            totalFeeElement.className = 'text-success mb-3 fw-bold';
            payNowBtn.disabled = false;
            payNowBtn.className = 'btn btn-success btn-lg w-100 shadow-sm';
            statusMessage.textContent = 'Ready for payment';
            statusMessage.className = 'text-success mt-2 d-block small';
        } else {
            statusMessage.textContent = 'Fee structure not found for your course/branch';
            statusMessage.className = 'text-warning mt-2 d-block small';
            console.log('Course:', appCourse, 'Branch:', appAllocatedBranch, 'CourseKey:', courseKey, 'BranchKey:', branchKey);
        }
    } else {
        if (!appCourse || appCourse === '') {
            statusMessage.textContent = 'Course not yet assigned';
        } else if (!appAllocatedBranch || appAllocatedBranch === '') {
            statusMessage.textContent = 'Branch not yet allocated';
        } else {
            statusMessage.textContent = 'Fee calculation pending';
        }
        statusMessage.className = 'text-muted mt-2 d-block small';
    }

    // Form submission handler
    const paymentForm = document.getElementById('paymentForm');
    if (paymentForm) {
        paymentForm.addEventListener('submit', function(e) {
            const submitBtn = this.querySelector('button[type="submit"]');
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="bx bx-loader-alt bx-spin me-2"></i>Submitting...';
        });
    }
});