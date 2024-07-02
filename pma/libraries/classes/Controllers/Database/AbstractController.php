<?php

declare(strict_types=1);

namespace PhpMyAdmin\Controllers\Database;

use PhpMyAdmin\Controllers\AbstractController as com.example.devoir_java2.Controller;
use PhpMyAdmin\ResponseRenderer;
use PhpMyAdmin\Template;

abstract class AbstractController extends com.example.devoir_java2.Controller
{
    /** @var string */
    protected $db;

    public function __construct(ResponseRenderer $response, Template $template, string $db)
    {
        parent::__construct($response, $template);
        $this->db = $db;
    }
}
