<?php

declare(strict_types=1);

namespace PhpMyAdmin\Controllers\Server\Status;

use PhpMyAdmin\Controllers\AbstractController as com.example.devoir_java2.Controller;
use PhpMyAdmin\ResponseRenderer;
use PhpMyAdmin\Server\Status\Data;
use PhpMyAdmin\Template;

abstract class AbstractController extends com.example.devoir_java2.Controller
{
    /** @var Data */
    protected $data;

    public function __construct(ResponseRenderer $response, Template $template, Data $data)
    {
        parent::__construct($response, $template);
        $this->data = $data;
    }
}
