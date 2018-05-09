<?php 

	function createFall($id, $event, $deviceip){
		define('DB_HOST', 'localhost');
	define('DB_USER', 'root');
	define('DB_PASS', 'root');
	define('DB_NAME', 'landingfall');
		$con = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);
	 
			//Checking if any error occured while connecting
			if (mysqli_connect_errno()) {
				echo "Failed to connect to MySQL: " . mysqli_connect_error();
			}
	 
			//finally returning the connection link 
			
		$stmt = $con->prepare("INSERT INTO falls (`id`, `event`, `deviceip`) VALUES (?, ?, ?)");
		$stmt->bind_param("ssis", $id, $event, $deviceip);
		if($stmt->execute())
			echo true; 
		echo false; 
	}
	function getFalls(){
		define('DB_HOST', 'localhost');
	define('DB_USER', 'root');
	define('DB_PASS', 'root');
	define('DB_NAME', 'landingfall');
		$con = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);
	 
			//Checking if any error occured while connecting
			if (mysqli_connect_errno()) {
				echo "Failed to connect to MySQL: " . mysqli_connect_error();
			}
	 
			//finally returning the connection link 
			
		$stmt = $con->prepare("SELECT id, event, deviceip FROM falls");
		$stmt->execute();
		$stmt->bind_result($id, $event, $deviceip);
		
		$falls = array(); 
		
		while($stmt->fetch()){
			$fall  = array();
			$fall['id'] = $id; 
			$fall['event'] = $event; 
			$fall['deviceip'] = $deviceip; 
			
			array_push($falls, $fall); 
		}
		
		return $falls; 
	}
	
	$response = array();
	
	if(isset($_GET['apicall'])){
		
		switch($_GET['apicall']){
			
			case 'createFall':
				
				$result = createHero(
					$_GET['id'],
					$_GET['event'],
					$_GET['deviceip'],
					
				);
				

				if($result){
					$response['error'] = false; 

					$response['message'] = 'Event registered successfully';

					$response['falls'] = '';
				}else{

					//if record is not added that means there is an error 
					$response['error'] = true; 

					//and we have the error message
					$response['message'] = 'Some error occurred please try again';
				}
				
			break; 
			
			case 'getFalls':
				$response['error'] = false; 
				$response['message'] = 'Request successfully completed';
				$response['falls'] = getFalls();
			break; 
		}
		
	}else{
		//if invalid call
		$response['error'] = true; 
		$response['message'] = 'Invalid API Call';
	}
	
	echo json_encode($response);
?>	
	
