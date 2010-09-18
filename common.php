<?php

//
function __autoload($class_name) {
  if (strpos($class_name, 'Page') === 0) {
    $pagename = substr($class_name, 4);
    $pagefile = strtolower($pagename[0]) . substr($pagename, 1);
    $pagefile = 'pages/' . $pagefile . '.php';
    //echo '<!-- loading page '.$class_name. ' in file "'.$pagefile. '" -->';
    if (file_exists($pagefile))
      require_once($pagefile);
    else
      echo '<!-- not found -->';
  } else {
    $classfile = 'types/type.' . $class_name . '.php';
    //echo '<!-- loading class '.$class_name. ' in file '.$classfile.' -->';
    if (file_exists($classfile))
      require_once($classfile);
    else {
      $classfile = 'types/html.' . $class_name . '.php';
      if (file_exists($classfile))
        require_once($classfile);
    }
  }
}

?>
