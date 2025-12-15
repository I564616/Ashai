
"use strict";

// Constant for AEST offset
const AEST_OFFSET_MINUTES = -600; // AEST is UTC+10

/**
 * Updates the visibility of the live chat button based on operating hours.
 * If the current time is within the operating hours, it initialises the live chat.
 */
function updateLiveChatButton() {
  try {
    const operatingHours = getOperatingHours();
    const currentTime = new Date();
    console.log("livechat.js file loaded");

    // Convert AEST to users local time
    const localOperatingHours = convertToLocalTime(operatingHours);

    // Check if current time is within operating hours
    if (
      currentTime >= localOperatingHours.start &&
      currentTime <= localOperatingHours.end
    ) {
      // If available, initialise chat
      initEmbeddedMessaging();
    } else {
      // Do not load live chat
    }
  } catch (error) {
    console.error("Error updating live chat button:", error);
  }
}

/**
 * Retrieves the operating hours from the DOM and validates them.
 * @returns {Object|null} An object containing start and end hours and minutes, or null if invalid.
 */
function getOperatingHours() {
  const fromHour = $("#livechatAvailableFromHour").val().padStart(2, "0");
  const fromMin = $("#livechatAvailableFromMin").val().padStart(2, "0");
  const toHour = $("#livechatAvailableToHour").val().padStart(2, "0");
  const toMin = $("#livechatAvailableToMin").val().padStart(2, "0");

  // Validate input values
  if (!isValidTime(fromHour, fromMin) || !isValidTime(toHour, toMin)) {
    console.error("Invalid operating hours provided.");
    return null;
  }

  return {
    startHours: fromHour,
    startMins: fromMin,
    endHours: toHour,
    endMins: toMin,
  };
}

/**
 * Validates the provided hours and minutes.
 * @param {string} hours - The hours to validate.
 * @param {string} minutes - The minutes to validate.
 * @returns {boolean} True if valid, false otherwise.
 */
function isValidTime(hours, minutes) {
  const hour = parseInt(hours, 10);
  const min = parseInt(minutes, 10);
  return hour >= 0 && hour < 24 && min >= 0 && min < 60;
}

/**
 * Converts operating hours from AEST to the user's local time.
 * @param {Object} operatingHours - The operating hours to convert.
 * @returns {Object} An object containing the local start and end times.
 */
function convertToLocalTime(operatingHours) {
  // Get the current date
  const currentDate = new Date();

  // Get the current timezone offset in minutes
  const currentOffset = currentDate.getTimezoneOffset(); // Offset in minutes from UTC

  // AEST offset is +10 hours from UTC
  const aestOffset = AEST_OFFSET_MINUTES;

  // Calculate the difference in minutes
  const differenceInMinutes = currentOffset - aestOffset;

  // Set local times
  const localStartTime = new Date();
  const localEndTime = new Date();
  localStartTime.setHours(operatingHours.startHours);
  localStartTime.setMinutes(operatingHours.startMins - differenceInMinutes);
  localEndTime.setHours(operatingHours.endHours);
  localEndTime.setMinutes(operatingHours.endMins - differenceInMinutes);

  return {
    start: localStartTime,
    end: localEndTime,
  };
}

/**
 * Initialises the embedded messaging service with the provided settings.
 */
function initEmbeddedMessaging() {
  try {
    embeddedservice_bootstrap.settings.language = "en_US"; // For example, enter 'en' or 'en-US'
    embeddedservice_bootstrap.init(
      $("#livechatMiawOrgId").val(),
      $("#livechatMiawDeploymentName").val(),
      $("#livechatMiawHostURL").val(),
      {
        scrt2URL: $("#livechatMiawScrtURL").val(),
      }
    );
  } catch (err) {
    console.error("Error loading Embedded Messaging: ", err);
  }
}

// Event listener for page load to update the live chat button
window.addEventListener("load", () => {
  updateLiveChatButton();
});

// Event listener for when the embedded messaging is ready
window.addEventListener("onEmbeddedMessagingReady", () => {

  resetLiveChatIfUserChanged();

  // Set invisible pre-chat field
  const firstName = $("#liveChatCustomDetails").attr("data-firstname");
  const lastName = $("#liveChatCustomDetails").attr("data-lastname");

  const email = $("#asahiStaffEmail").val() || $("#liveChatCustomDetails").attr("data-email");
  const userPK = $("#liveChatCustomDetails").attr("data-userpk");

  const businessUnit = $("#livechatMiawSiteNameALB").val();

  const accountNumberRaw = $("#liveChatCustomDetails").attr(
    "data-currentB2Bunit-id"
  );

  const accountNumber = Number(accountNumberRaw).toString(); // Trim leading zeros

  const venue =
    accountNumber +
    "-" +
    $("#liveChatCustomDetails").attr("data-currentB2Bunit-name");

  embeddedservice_bootstrap.prechatAPI.setHiddenPrechatFields({
    FirstName: firstName, // to be populated by portal with first name of chat user
    LastName: lastName, // to be populated by portal with last name of chat user
    Email: email, // to be populated by portal with email of chat user
    CustomerAccountNumber: accountNumber, // to be populated by portal with ECC id of Account
    BusinessUnit: businessUnit, // to be populated by portal with business unit of chat user
    ContactPK: userPK, // to be populated by portal with PK (primary key) of chat user to be used to match with a Salesforce Contact record
    Venue: venue,
  });
});

// Function to clear livechat session on link click
function handleLinkClick(event) {
  embeddedservice_bootstrap.userVerificationAPI.clearSession(true);
}

// Event listeners for link clicks
$(document).ready(function() {

  /* CUB */
  // Handle click event for the B2B link
  $('.company-name a').on('click', function(event) {
    handleLinkClick(event);
  });

  // Handle click event for the Sign Out link
  $('.logout-link .highlight-link').on('click', function(event) {
    handleLinkClick(event);
  });

  /* ALB */
  // Handle click event for the B2B link - ALB
  $('.multiAccount-button').on('click', function(event) {
      handleLinkClick(event);
  });

  // Handle click event for Sign Out Link
  $('#header-nav-logout-text').on('click', function(event) {
    handleLinkClick(event);
  });
});


function resetLiveChatIfUserChanged() {
  const email = $("#asahiStaffEmail").val() || $("#liveChatCustomDetails").data("email");
  const userPK = $("#liveChatCustomDetails").data("userpk");
  const accountId = $("#liveChatCustomDetails").data("currentb2bunit-id");

  // Construct a unique key for this user
  const userKey = [email, userPK, accountId].join("|");

  // Retrieve previous user key from localStorage
  const prevUserKey = localStorage.getItem("liveChatUserKey");

  // If there's a previous key and it's different, clear session
  if (prevUserKey && prevUserKey !== userKey) {
    embeddedservice_bootstrap?.userVerificationAPI?.clearSession?.(true);
  }
  // Save current user key for next time
  localStorage.setItem("liveChatUserKey", userKey);
}