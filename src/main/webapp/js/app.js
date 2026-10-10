/* =====================================================================
   QuizDesk - front-end behaviour
   Progressive enhancement only: every page works with JavaScript
   disabled, the scripts just make it nicer to use.
   ===================================================================== */
(function () {
    "use strict";

    /* ---------- Mobile navigation ---------------------------------- */
    function initNavToggle() {
        var toggle = document.querySelector(".nav-toggle");
        var links = document.querySelector(".nav-links");
        if (!toggle || !links) { return; }

        toggle.addEventListener("click", function () {
            var isOpen = links.classList.toggle("open");
            toggle.setAttribute("aria-expanded", isOpen ? "true" : "false");
        });
    }

    /* ---------- Quiz attempt -------------------------------------- */

    /** Highlights the chosen option and keeps the question navigator in sync. */
    function initQuizOptions() {
        var options = document.querySelectorAll(".option input[type=radio]");
        if (!options.length) { return; }

        options.forEach(function (input) {
            input.addEventListener("change", function () {
                // Clear the highlight from every option of this question, then set the new one.
                var group = document.querySelectorAll('.option input[name="' + input.name + '"]');
                group.forEach(function (sibling) {
                    sibling.closest(".option").classList.remove("selected");
                });
                input.closest(".option").classList.add("selected");
                markNavigatorAnswered(input.name);
            });
        });
    }

    function markNavigatorAnswered(inputName) {
        var button = document.querySelector('.q-nav-btn[data-target="' + inputName + '"]');
        if (button) {
            button.classList.add("answered");
            button.classList.remove("current");
        }
    }

    function initQuestionNavigator() {
        var buttons = document.querySelectorAll(".q-nav-btn");
        if (!buttons.length) { return; }

        buttons.forEach(function (button) {
            button.addEventListener("click", function () {
                var target = document.getElementById(button.dataset.target);
                if (target) {
                    target.scrollIntoView({ behavior: "smooth", block: "start" });
                }
                buttons.forEach(function (other) { other.classList.remove("current"); });
                button.classList.add("current");
            });
        });
    }

    /** Previous / Next move through the stacked question cards. */
    function initPrevNext() {
        var cards = document.querySelectorAll(".question-card");
        if (!cards.length) { return; }

        var index = 0;
        var currentLabel = document.getElementById("current-question");

        function show(newIndex) {
            index = Math.max(0, Math.min(cards.length - 1, newIndex));
            cards[index].scrollIntoView({ behavior: "smooth", block: "start" });
            if (currentLabel) { currentLabel.textContent = String(index + 1); }
            var prev = document.getElementById("btn-prev");
            var next = document.getElementById("btn-next");
            if (prev) { prev.disabled = index === 0; }
            if (next) { next.disabled = index === cards.length - 1; }
        }

        var prev = document.getElementById("btn-prev");
        var next = document.getElementById("btn-next");
        if (prev) { prev.addEventListener("click", function () { show(index - 1); }); }
        if (next) { next.addEventListener("click", function () { show(index + 1); }); }
        show(0);
    }

    /**
     * Asks before submitting, and reports how many questions are still unanswered.
     * This is the guard against accidental submission.
     */
    function initSubmitConfirmation() {
        var form = document.getElementById("quiz-form");
        if (!form) { return; }

        form.addEventListener("submit", function (event) {
            var cards = form.querySelectorAll(".question-card");
            var answered = 0;
            cards.forEach(function (card) {
                if (card.querySelector("input[type=radio]:checked")) {
                    answered++;
                }
            });

            var unanswered = cards.length - answered;
            var message = "You have answered " + answered + " of " + cards.length + " questions.";
            if (unanswered > 0) {
                message += "\n" + unanswered + " question" + (unanswered === 1 ? " is" : "s are")
                         + " still unanswered. Unanswered questions score zero.";
            }
            message += "\n\nSubmit your quiz?";

            if (!window.confirm(message)) {
                event.preventDefault();
            }
        });
    }

    /**
     * Countdown timer. The end time is computed once from the server-provided duration, so
     * reloading the page cannot buy extra time; the server remains the authority on scoring.
     */
    function initTimer() {
        var timer = document.getElementById("quiz-timer");
        if (!timer) { return; }

        var minutes = parseInt(timer.dataset.minutes, 10);
        if (!minutes || minutes <= 0) { return; }

        var deadline = Date.now() + minutes * 60 * 1000;

        function tick() {
            var remaining = Math.max(0, deadline - Date.now());
            var totalSeconds = Math.floor(remaining / 1000);
            var mm = String(Math.floor(totalSeconds / 60)).padStart(2, "0");
            var ss = String(totalSeconds % 60).padStart(2, "0");
            timer.textContent = mm + ":" + ss;

            if (totalSeconds <= 60) {
                timer.classList.add("warning");
            }
            if (remaining === 0) {
                timer.textContent = "Time up";
                var form = document.getElementById("quiz-form");
                if (form) {
                    window.alert("Time is up. Your quiz will be submitted now.");
                    form.submit();
                }
                return;
            }
            window.setTimeout(tick, 1000);
        }
        tick();
    }

    /* ---------- Destructive actions -------------------------------- */

    /** Any element with data-confirm asks once before proceeding. */
    function initConfirmActions() {
        document.querySelectorAll("[data-confirm]").forEach(function (element) {
            element.addEventListener("click", function (event) {
                if (!window.confirm(element.dataset.confirm)) {
                    event.preventDefault();
                }
            });
        });
    }

    /** Admin: pick which option is correct with one click. */
    function initCorrectOptionPicker() {
        var hidden = document.getElementById("correctOption");
        if (!hidden) { return; }

        document.querySelectorAll(".option-pick").forEach(function (button) {
            button.addEventListener("click", function () {
                document.querySelectorAll(".option-pick").forEach(function (other) {
                    other.classList.remove("btn-primary");
                    other.classList.add("btn-secondary");
                    other.setAttribute("aria-pressed", "false");
                });
                button.classList.remove("btn-secondary");
                button.classList.add("btn-primary");
                button.setAttribute("aria-pressed", "true");
                hidden.value = button.dataset.key;
            });
        });
    }

    /** Blocks the second click so a slow save cannot be submitted twice. */
    function initSubmitLock() {
        document.querySelectorAll("form[data-lock-on-submit]").forEach(function (form) {
            form.addEventListener("submit", function () {
                var button = form.querySelector("button[type=submit]");
                if (button) {
                    window.setTimeout(function () {
                        button.disabled = true;
                        button.textContent = "Saving...";
                    }, 0);
                }
            });
        });
    }

    /* ---------- Boot ---------------------------------------------- */
    document.addEventListener("DOMContentLoaded", function () {
        initNavToggle();
        initQuizOptions();
        initQuestionNavigator();
        initPrevNext();
        initSubmitConfirmation();
        initTimer();
        initConfirmActions();
        initCorrectOptionPicker();
        initSubmitLock();
    });
})();
