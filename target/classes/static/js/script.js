const togglesidebar = () => {
	if ($(".sidebar").is(":visible")) {

		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "3%");
		$(".bars").css("display", "inline-block");
		$(".bars").css("margin-top", "10px");

	} else {
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");

	}
}

const search = () => {
	let query = $("#search-input").val();
	if (query == '') {
		$(".search-result").hide();
	}
	else {

		//search
		//we are using here `` and not "" called as backticks
		let url = `http://localhost:8080/search/${query}`;

		/*fetch returns a promise that resolves to the Response object representing the response to the request. */
		fetch(url)
			.then((response) => {
				return response.json();
			})
			.then((data) => {

				let text = `<div class='list-group'>`;

				data.forEach(contact => {
					text += `<a href='/user/particularcontact/${contact.id}' class='list-group-item list-group-item-action'>${contact.name} </a>`
				});

				text += `</div>`
				$(".search-result").html(text);
				$(".search-result").show();
			});

	}
}

const paymentStart = () => {

	let amount = $("#pid").val()
	if (amount == '' || amount == null) {
		swal("Failed", "Amount required!", "error");
		return;
	}

	//we will use ajax to send req to server to create order
	$.ajax({
		url: '/user/createorder',
		data: JSON.stringify({ amount: amount, info: 'orderreq' }),
		contentType: 'application/json',
		type: 'POST',

		success: function(response) {
			//invoked when success
			let parsedResponse = JSON.parse(response);  //as responce is in string we need to parse it

			if (parsedResponse.status == 'created') {
				//open payment form
				var options = {
					"key": "rzp_test_PKJksoyRMSaVq8", // Enter the Key ID generated from the Dashboard
					"amount": parsedResponse.amount, // Amount is in currency subunits. Default currency is INR. Hence, 50000 refers to 50000 paise
					"currency": "INR",
					"name": "Smart Contact Manager",
					"description": "Test Transaction",
					"image": "data:image/jpeg;base64,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",
					"order_id": parsedResponse.id, //This is a sample Order ID. Pass the `id` obtained in the response of Step 1
					"handler": function(response) {
						console.log(response.razorpay_payment_id);
						console.log(response.razorpay_order_id);
						console.log(response.razorpay_signature);
						Swal.fire({
							position: "center",
							icon: "success",
							title: "Payment Succesfull",
							showConfirmButton: false,
							timer: 1500
						});
					},
					"prefill": {
						"name": "",
						"email": "",
						"contact": ""
					},
					"notes": {
						"address": "Razorpay Corporate Office"
					},
					"theme": {
						"color": "#3399cc"
					}
				};

				var rzp1 = new Razorpay(options);
				rzp1.on('payment.failed', function(response) {
					console.log(response.error.code);
					console.log(response.error.description);
					console.log(response.error.source);
					console.log(response.error.step);
					console.log(response.error.reason);
					console.log(response.error.metadata.order_id);
					console.log(response.error.metadata.payment_id);
					Swal.fire({
						icon: "error",
						title: "Oops...",
						text: "Something went wrong!",

					});
				});

				rzp1.open();

			}
		},
		error: function(error) {
			//invoke when failure
			console.log("something went wrong!!")
		}
	})
};