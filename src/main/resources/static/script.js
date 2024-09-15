function validateSalaryInput(input) {
    input.value = input.value.replace(/[^0-9.]/g, '');
    const parts = input.value.split('.');

    if (parts.length > 2) {
        input.value = input.value.replace(/\.+$/, '');
    }

    if (parts[1] && parts[1].length > 2) {
        input.value = parts[0] + '.' + parts[1].substring(0, 2);
    }
}

function validateVacationDaysInput(input) {
    input.value = input.value.replace(/[^0-9]/g, '');
}

document.addEventListener("DOMContentLoaded", function() {
    const calculateButton = document.getElementById("calculate");
    const averageMonthlySalary = document.getElementById("avgMonthSalary");
    const vacationDays = document.getElementById("vacationDays");
    const startDate = document.getElementById("startDate");
    const endDate = document.getElementById("endDate");

    calculateButton.addEventListener("click", function(event) {
        event.preventDefault();

        const salaryValue = parseFloat(averageMonthlySalary.value);
        const vacationDaysValue = parseInt(vacationDays.value, 10);
        const startDateValue = new Date(startDate.value);
        const endDateValue = new Date(endDate.value);

        if (isNaN(salaryValue)) {
            document.getElementById("result").innerHTML = "Введите числовое значение";
            return;
        }

        let days;
        if (startDate.value && endDate.value) {
            if (startDateValue > endDateValue) {
                document.getElementById("result").innerHTML = "Дата окончания отпуска не может быть раньше даты начала";
                return;
            }
            const diffTime = Math.abs(endDateValue - startDateValue);
            days = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
        } else if (!isNaN(vacationDaysValue)) {
            days = vacationDaysValue;
        } else {
            document.getElementById("result").innerHTML = "Введите количество дней отпуска или выберите диапазон дат";
            return;
        }

        if (salaryValue < 0 || days < 0) {
            document.getElementById("result").innerHTML = "Ошибка: вводимые значения не могут быть отрицательными";
            return;
        }

        /*if (!Number.isInteger(vacationDaysValue)) {
            document.getElementById("result").innerHTML = "Ошибка: количество дней отпуска должно быть целым числом";
            return;
        }*/

        const data = {
            averageMonthlySalary: salaryValue,
            vacationDays: days
        };

        fetch("/calculate", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        })
            .then(response => response.json())
            .then(result => {
                document.getElementById("result").innerHTML = `Размер отпускных: ${result}`;
            })
            .catch(error => {
                console.error('Error:', error);
                document.getElementById("result").innerHTML = "Произошла ошибка при расчете";
            });
    });
});