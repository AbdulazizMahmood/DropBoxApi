var accessToken = "";
var account_id = "";
var uploadFilePath = "";

window.onload = customize;

function customize() {
	window.document.getElementById('b3').disabled = true;
	window.document.getElementById('b4').disabled = true;
	window.document.getElementById('b5').disabled = true;
	window.document.getElementById('profileImageInput').disabled = true;
	window.document.getElementById('setProfileButton').disabled = true;
	window.document.getElementById('b6').disabled = true;
	window.document.getElementById('div100').disabled = true;
	window.document.getElementById('div8').style.display = 'none';

	if (window.location.href.substring(35).length > 0) {
		window.document.getElementById('b1').disabled = true;
		window.document.getElementById('b2').disabled = false;
	} else {
		window.document.getElementById('b2').disabled = true;
		window.document.getElementById('b1').disabled = false;
	}

	// Event listeners for image input and set profile button
	document.getElementById('profileImageInput').addEventListener('change', handleImageChange);
	document.getElementById('setProfileButton').addEventListener('click', uploadProfilePhoto);
}

function handleImageChange(event) {
	var file = event.target.files[0];
	if (file) {
		var reader = new FileReader();
		reader.onloadend = function() {
			selectedProfileImageBase64 = reader.result.split(',')[1]; // Extract base64 string
			console.log('Base64 image data:', selectedProfileImageBase64); // Log the base64 string
		};
		reader.readAsDataURL(file); // Read file as Data URL to get base64
	}
}

function doQuery() {
	var q_str = 'reqType=doQuery';
	doAjax('DBoxClientMediator', q_str, 'doQuery_back', 'post', 0);
}

function doQuery_back(result) {
	window.location = result;
}

function doQuery1() {
	var q_str = 'reqType=doQuery1';
	if (window.location.href.substring(41).length > 0) {
		q_str += '&code=' + window.location.href.substring(41);
		doAjax('DBoxClientMediator', q_str, 'doQuery1_back', 'post', 0);
	} else {
		alert('Please connect first...');
	}
}

function doQuery1_back(result) {
	var json = result;
	json_obj = JSON.parse(json);

	accessToken = json_obj.access_token;
	account_id = json_obj.account_id;

	var html_cont = "";
	for (var x in json_obj) {
		if (json_obj.hasOwnProperty(x)) {
			html_cont += "<tr><td align='right'><i>" + x + "</i>:</td><td>&nbsp</td><td align='left'>" + json_obj[x] + "</td></tr>";
		}
	}


	html_cont = "<table>" + html_cont + "</table>";
	window.document.getElementById('div3').innerHTML = html_cont;

	window.document.getElementById('b2').disabled = true;
	window.document.getElementById('b3').disabled = false;

	// Enable the profile image input and set profile button
	window.document.getElementById('profileImageInput').disabled = false;
	window.document.getElementById('setProfileButton').disabled = false;
	window.document.getElementById('b6').disabled = false;
	window.document.getElementById('div100').disabled = false;

	// Show the div9 section
	window.document.getElementById('div9').style.display = 'block';
}

function doQuery2() {
	window.document.getElementById('div3').style.display = 'none';
	var q_str = 'reqType=doQuery2';
	q_str += '&access_token=' + accessToken;
	q_str += '&account_id=' + account_id;
	doAjax('DBoxClientMediator', q_str, 'doQuery2_back', 'post', 0);
}

function doQuery2_back(result) {
	var json = result;
	json_obj = JSON.parse(json);
	var html_cont = fromObjToTable(json_obj);
	window.document.getElementById('div5').innerHTML = html_cont;

	window.document.getElementById('b3').disabled = true;
	window.document.getElementById('b4').disabled = false;
}

function fromObjToTable(obj) {
	var html_cont = "";
	for (var x in obj) {
		if (obj.hasOwnProperty(x)) {
			if (obj[x] instanceof Object) {
				html_cont += "<tr><td align='right'><i>" + x + "</i>:</td><td>&nbsp</td><td align='left'>" + fromObjToTable(obj[x]) + "</td></tr>";
			} else {
				html_cont += "<tr><td align='right'><i>" + x + "</i>:</td><td>&nbsp</td><td align='left'>" + obj[x] + "</td></tr>";
			}
		}
	}
	html_cont = "<table>" + html_cont + "</table>";
	return html_cont;
}

function doQuery3_() {
	window.document.getElementById('div5').style.display = 'none';
	uploadFilePath = "images/image_initial.png";
	var html_cont = "<table><tr><td><table><tr><td align='left'><i>Initial image</i>: " + uploadFilePath + "</td></tr><tr><td align='left'><img src='" + uploadFilePath + "' style='width:200px;height:200px;'></td></tr></table></td><td><table><tr><td></td></tr><tr><td></td></tr></table></td></tr></table>";
	window.document.getElementById('div7').innerHTML = html_cont;
	window.document.getElementById('b4').disabled = true;
	window.document.getElementById('div6').style.display = 'none';
	window.document.getElementById('div8').style.display = 'block';
	window.document.getElementById('b5').disabled = false;
}

function doQuery3() {
	var q_str = 'reqType=doQuery3';
	q_str += '&access_token=' + accessToken;
	q_str += '&filePath=' + uploadFilePath;
	doAjax('DBoxClientMediator', q_str, 'doQuery3_back', 'post', 0);
}

function doQuery3_back(result) {
	var json = result;
	json_obj = JSON.parse(json);
	var html_c = fromObjToTable(json_obj);
	var html_cont = "<table><tr><td><table><tr><td align='left'><i>Initial image</i>: " + uploadFilePath + "</td></tr><tr><td align='left'><img src='" + uploadFilePath + "' style='width:200px;height:200px;'></td></tr></table></td><td><table><tr><td><i>Uploaded image</i>: image_initial_uploaded.png</td></tr><tr><td>" + html_c + "</td></tr></table></td></tr></table>";
	window.document.getElementById('div7').innerHTML = html_cont;
	window.document.getElementById('b5').disabled = true;
}




function doQuery4() {
	var folderName = prompt("Enter the folder name:"); // Prompt for folder name
	//var folderName = "Test Folder";
	if (folderName) {
		var q_str = 'reqType=doQuery4'; // Request type for the servlet
		q_str += '&access_token=' + accessToken; // Include access token
		q_str += '&folderPath=' + encodeURIComponent('/' + folderName); // Use encodeURIComponent for safety

		// Call the AJAX function
		doAjax('DBoxClientMediator', q_str, 'doQuery4_back', 'post', 0);
	} else {
		alert("Folder name is required.");
	}
}

function doQuery4_back(result) {
	// Handle the response from the servlet
	console.log("Create Folder Response:", result);
	var jsonResponse = JSON.parse(result);

	// You can display a message or update the UI based on the response
	if (jsonResponse.error) {
		alert("Error creating folder: " + jsonResponse.error);
	} else {
		alert("Folder created successfully: " + jsonResponse.name);
	}
}





var selectedProfileImageBase64 = "";

// Function to upload the selected profile photo
function uploadProfilePhoto() {
	if (!selectedProfileImageBase64) {
		alert("Please select an image first.");
		return;
	}

	var q_str = 'reqType=setProfilePhoto';
	q_str += '&access_token=' + accessToken;

	// Prepare the JSON body for the API request
	var requestBody = {
		"reqType": "setProfilePhoto",  // Adding the reqType for request identification
		"access_token": accessToken,   // The access token for authentication
		"photo": {
			".tag": "base64_data",     // Specifies the format of the photo data
			"base64_data": selectedProfileImageBase64  // The base64-encoded image data
		}
	};
	console.log(requestBody);

	// Send the AJAX request to the server
	doAjax('DBoxJsonMediator', JSON.stringify(requestBody), 'uploadProfilePhoto_back', 'postJSON', 0);
}

// Function to handle the response from the server
function uploadProfilePhoto_back(result) {
	var json = result;
	json_obj = JSON.parse(json);

	if (json_obj.profile_photo_url) {
		var html_cont = "<p>Profile photo updated successfully.</p>";
		html_cont += "<p>New Profile Photo:</p>";
		html_cont += "<img src='" + json_obj.profile_photo_url + "' alt='Profile Photo' style='width:128px;height:128px;'>";
		window.document.getElementById('div10').innerHTML = html_cont;
	} else {
		window.document.getElementById('div10').innerHTML = "<p>Error updating profile photo.</p>";
	}
}


var downloadPath = 'D:\\'
function formatDateToCustomFormat(dateString) {
	const date = new Date(dateString);

	// Get components
	const hours = date.getHours();
	const minutes = date.getMinutes();
	const day = date.getDate();
	const month = date.getMonth() + 1; // Months are zero-based
	const year = date.getFullYear() % 100; // Last two digits of the year

	// Convert to 12-hour format and AM/PM
	const formattedHours = hours % 12 || 12; // Convert to 12-hour format
	const formattedMinutes = minutes < 10 ? '0' + minutes : minutes; // Pad minutes with leading zero
	const ampm = hours >= 12 ? 'PM' : 'AM';

	// Format final string
	return `${formattedHours}.${formattedMinutes} ${ampm}, ${day < 10 ? '0' + day : day}/${month < 10 ? '0' + month : month}/${year}`;
}
function doDownload(path) {
	console.log('download from doDownload')

	var q_str = 'reqType=downloadFile';
	q_str += '&access_token=' + accessToken;
	q_str += '&filePath=' + path;
	q_str += '&downloadPath=' + downloadPath;
	doAjax('DBoxClientMediator', q_str, 'downloadFile_back', 'post', 0);
}

function downloadFile_back(result) {
	alert('Download result: ' + result);
	var json = JSON.parse(result);
	document.getElementById('downloadResult').innerHTML = "Successfully downloaded to: " + downloadPath;
}


function listDropboxItems() {
	var folderPath = "/mydboxclient_app01_files/images"
	var q_str = 'reqType=listDropboxItems';
	q_str = q_str + '&access_token=' + accessToken;
	q_str = q_str + '&folderPath=' + folderPath;
	doAjax('DBoxClientMediator', q_str, 'listDropboxItems_back', 'post', 0);
}

function listDropboxItems_back(result) {
	var json = result;
	json_obj = JSON.parse(json);
	console.log('reslt', json_obj)
	var items = json_obj.entries
	var tableBody = document.getElementById('listItem');
	tableBody.innerHTML = "";

	items.forEach(function(item) {
		var row = tableBody.insertRow();

		var typeCell = row.insertCell(0);
		typeCell.textContent = item[".tag"] === "folder" ? "Folder" : "File";

		var nameCell = row.insertCell(1);
		nameCell.textContent = item.name;

		var pathCell = row.insertCell(2);
		pathCell.textContent = item[".tag"] !== "folder" ? formatDateToCustomFormat(item.client_modified) : '-';
		var pathCell = row.insertCell(3);
		pathCell.textContent = item.size ? `${(item.size / 1024).toFixed(2)} KB` : '';
		var buttonCell = row.insertCell(4);


		var viewButton = document.createElement('button');
		viewButton.textContent = 'Get MetaData';
		viewButton.onclick = function() {
			console.log('Viewing:', item.name);
			getMetadata(item.path_display)
		};

		var downloadButton = document.createElement('button');
		downloadButton.textContent = 'Download';
		downloadButton.onclick = function() {
			console.log('Downloading:', item.name);
			doDownload(item.path_display)
		};

		var deleteButton = document.createElement('button');
		deleteButton.textContent = 'Delete';
		deleteButton.onclick = function() {
			console.log('Deleting:', item.name);
			doQuery5(item.path_display)
		};
		if (item[".tag"] !== "folder") {
			buttonCell.appendChild(viewButton);
			buttonCell.appendChild(downloadButton);
			buttonCell.appendChild(deleteButton);
		}
	});
}


function getMetadata(filePath) {
	console.log('inside')
	//var filePath = "/MyDBoxClient_App01_files/images/testimage.jpg" 
	var q_str = 'reqType=getMetadata';
	q_str = q_str + '&access_token=' + accessToken;
	q_str = q_str + '&filePath=' + filePath;
	doAjax('DBoxClientMediator', q_str, 'getMetadata_back', 'post', 0);


}


function getMetadata_back(result) {
	// alert('Metadata result: ' + result);

	var json = JSON.parse(result);
	document.getElementById('div12').innerHTML = "The metadata is: " + result;
}
