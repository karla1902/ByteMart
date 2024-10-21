(function($) {
	"use strict"
	document.addEventListener("DOMContentLoaded", function() {
		const slides = document.querySelectorAll('.banner-slide');
		let currentIndex = 0;
	
		function showNextSlide() {
			slides[currentIndex].classList.remove('active');
			currentIndex = (currentIndex + 1) % slides.length;
			slides[currentIndex].classList.add('active');
		}
	
		setInterval(showNextSlide, 3000); // Cambia la imagen cada 3 segundos
	});
	

		// Animation Login/Register
	const signUpButton = document.getElementById('signUp');
	const signInButton = document.getElementById('signIn');
	const container = document.getElementById('container');

	if (signUpButton && signInButton && container) {
		signUpButton.addEventListener('click', () => {
			container.classList.add("right-panel-active");
		});

		signInButton.addEventListener('click', () => {
			container.classList.remove("right-panel-active");
		});
	}

	document.getElementById('request-reset-form').addEventListener('submit', function(e) {
	e.preventDefault();

	const email = document.getElementById('userEmail').value;

	fetch('/sendResetCode', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({ email: email })
	})
	.then(response => response.json())
	.then(data => {
		if (data.success) {
			document.getElementById('reset-password-form').style.display = 'block';
			document.getElementById('request-reset-form').style.display = 'none';
		} else {
			alert('Error al enviar el correo. Inténtalo de nuevo.');
		}
	});
	});


	document.getElementById('reset-password-form').addEventListener('submit', function(e) {
			e.preventDefault();

			const code = document.getElementById('resetCode').value;
			const newPassword = document.getElementById('newPassword').value;
			const confirmPassword = document.getElementById('confirmPassword').value;

			fetch('/resetPassword', {
					method: 'POST',
					headers: {
							'Content-Type': 'application/json'
					},
					body: JSON.stringify({ resetCode: code, newPassword: newPassword, confirmPassword: confirmPassword })
			})
			.then(response => response.json())
			.then(data => {
					if (data.success) {
							$('#ModalPassword').modal('show');
					} else {
							$('#ModalPasswordError').modal('show');
					}
			});
	});

	// Mobile Nav toggle
	$('.menu-toggle > a').on('click', function (e) {
		e.preventDefault();
		$('#responsive-nav').toggleClass('active');
	})

	// Fix cart dropdown from closing
	$('.cart-dropdown').on('click', function (e) {
		e.stopPropagation();
	});

	/////////////////////////////////////////

	// Products Slick
	$('.products-slick').each(function() {
		var $this = $(this),
				$nav = $this.attr('data-nav');

		$this.slick({
			slidesToShow: 4,
			slidesToScroll: 1,
			autoplay: true,
			infinite: true,
			speed: 300,
			dots: false,
			arrows: true,
			appendArrows: $nav ? $nav : false,
			responsive: [{
	        breakpoint: 991,
	        settings: {
	          slidesToShow: 2,
	          slidesToScroll: 1,
	        }
	      },
	      {
	        breakpoint: 480,
	        settings: {
	          slidesToShow: 1,
	          slidesToScroll: 1,
	        }
	      },
	    ]
		});
	});

	// Products Widget Slick
	$('.products-widget-slick').each(function() {
		var $this = $(this),
				$nav = $this.attr('data-nav');

		$this.slick({
			infinite: true,
			autoplay: true,
			speed: 300,
			dots: false,
			arrows: true,
			appendArrows: $nav ? $nav : false,
		});
	});

	/////////////////////////////////////////

	// Product Main img Slick
	$('#product-main-img').slick({
    infinite: true,
    speed: 300,
    dots: false,
    arrows: true,
    fade: true,
    asNavFor: '#product-imgs',
  });

	// Product imgs Slick
  $('#product-imgs').slick({
    slidesToShow: 3,
    slidesToScroll: 1,
    arrows: true,
    centerMode: true,
    focusOnSelect: true,
		centerPadding: 0,
		vertical: true,
    asNavFor: '#product-main-img',
		responsive: [{
        breakpoint: 991,
        settings: {
					vertical: false,
					arrows: false,
					dots: true,
        }
      },
    ]
  });

	// Product img zoom
	var zoomMainProduct = document.getElementById('product-main-img');
	if (zoomMainProduct) {
		$('#product-main-img .product-preview').zoom();
	}

	/////////////////////////////////////////

	// Input number
	$('.input-number').each(function() {
		var $this = $(this),
		$input = $this.find('input[type="number"]'),
		up = $this.find('.qty-up'),
		down = $this.find('.qty-down');

		down.on('click', function () {
			var value = parseInt($input.val()) - 1;
			value = value < 1 ? 1 : value;
			$input.val(value);
			$input.change();
			updatePriceSlider($this , value)
		})

		up.on('click', function () {
			var value = parseInt($input.val()) + 1;
			$input.val(value);
			$input.change();
			updatePriceSlider($this , value)
		})
	});

	var priceInputMax = document.getElementById('price-max'),
			priceInputMin = document.getElementById('price-min');

	priceInputMax.addEventListener('change', function(){
		updatePriceSlider($(this).parent() , this.value)
	});

	priceInputMin.addEventListener('change', function(){
		updatePriceSlider($(this).parent() , this.value)
	});

	function updatePriceSlider(elem , value) {
		if ( elem.hasClass('price-min') ) {
			console.log('min')
			priceSlider.noUiSlider.set([value, null]);
		} else if ( elem.hasClass('price-max')) {
			console.log('max')
			priceSlider.noUiSlider.set([null, value]);
		}
	}

	// Price Slider
	var priceSlider = document.getElementById('price-slider');
	if (priceSlider) {
		noUiSlider.create(priceSlider, {
			start: [1, 999],
			connect: true,
			step: 1,
			range: {
				'min': 1,
				'max': 999
			}
		});

		priceSlider.noUiSlider.on('update', function( values, handle ) {
			var value = values[handle];
			handle ? priceInputMax.value = value : priceInputMin.value = value
		});
	}

})(jQuery);


// main.js

document.addEventListener('DOMContentLoaded', function() {
    const regionSelect = document.getElementById('region');
    const provinciaSelect = document.getElementById('provincia');
    const comunaSelect = document.getElementById('comuna');

    if (regionSelect.value) {
        loadProvincias(); // Llama a loadProvincias si ya hay una región seleccionada
    }
    
    if (provinciaSelect.value) {
        loadComunass(); // Llama a loadComunass si ya hay una provincia seleccionada
    }
});

function loadProvincias() {
    const regionSelect = document.getElementById('region');
    const provinciaSelect = document.getElementById('provincia');
    const comunaSelect = document.getElementById('comuna');
    
    const selectedRegionId = regionSelect.value;
    provinciaSelect.innerHTML = '<option value="">Seleccione una provincia</option>';
    comunaSelect.innerHTML = '<option value="">Seleccione una comuna</option>';

    if (selectedRegionId) {
        fetch(`http://127.0.0.1:5005/provincias/${selectedRegionId}`)
            .then(response => response.json())
            .then(data => {
                data.provincias.forEach(provincia => {
                    const option = document.createElement('option');
                    option.value = provincia.codigo;
                    option.textContent = provincia.nombre;
                    provinciaSelect.appendChild(option);
                });
            });
    }
}

function loadComunass() {
    const provinciaSelect = document.getElementById('provincia');
    const comunaSelect = document.getElementById('comuna');

    const selectedProvinciaId = provinciaSelect.value;
    comunaSelect.innerHTML = '<option value="">Seleccione una comuna</option>';

    if (selectedProvinciaId) {
        fetch(`http://127.0.0.1:5005/comunas/${selectedProvinciaId}`)
            .then(response => response.json())
            .then(data => {
                data.comunas.forEach(comuna => {
                    const option = document.createElement('option');
                    option.value = comuna.codigo;
                    option.textContent = comuna.nombre;
                    comunaSelect.appendChild(option);
                });
            });
    }
}

function fillForm(id, nombre, apellido, username, email, direccion, is_admin) {
    document.querySelector('input[name="id"]').value = id;
    document.getElementById('nombre').value = nombre;
    document.getElementById('apellido').value = apellido;
    document.getElementById('username').value = username;
    document.getElementById('email').value = email;
    document.getElementById('nombre_direccion').value = direccion; // Corregido
    document.getElementById('is_admin').checked = is_admin; // Asegúrate de tener el checkbox
}







