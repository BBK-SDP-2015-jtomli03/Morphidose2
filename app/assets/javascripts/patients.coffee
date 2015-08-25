$ ->
  $.get "/patients", (patients) ->
    $.each patients, (index, patient) ->
      hospitalNumber = $("<div>").addClass("hospitalNumber").text patient.hospitalNumber
      title = $("<div>").addClass("title").text patient.title
      firstName = $("<div>").addClass("firstName").text patient.firstName
      surname = $("<div>").addClass("surname").text patient.surname
      dob = $("<div>").addClass("dob").text patient.dob
      $("#patients").append $("<li>").append(title).append(firstName).append(surname).append(hospitalNumber).append(dob)
