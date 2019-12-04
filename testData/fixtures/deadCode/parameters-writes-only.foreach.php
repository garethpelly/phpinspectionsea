<?php

function cases_holder() {
    foreach ([] as $unused1) {
        <weak_warning descr="[EA] Parameter/variable is overridden, but is never used or appears outside of the scope.">$unused1[]</weak_warning> = '...';
        <weak_warning descr="[EA] Parameter/variable is overridden, but is never used or appears outside of the scope.">$unused1['...']</weak_warning> = '...';
    }

    foreach ([] as $unused2) {
        <weak_warning descr="[EA] Parameter/variable is overridden, but is never used or appears outside of the scope.">$unused2['...']</weak_warning> = '...';
        unset(<weak_warning descr="[EA] Parameter/variable is overridden, but is never used or appears outside of the scope.">$unused2['...']</weak_warning>);
    }
}

function false_positives_holder($object) {
    $used1 = [];
    $used2 = [];
    $used3 = [];
    foreach ([] as $v) {
        $used1[] = '...';
        $used2[] = '...';
        $used3[] = '...';
    }
    $object->property += $used1;
    $object['...'] += $used2;
    $object['...']['...'] += $used3;

    foreach ([] as & $array) {
        $array[] = '...';
    }

    foreach ([] as & $array) {
        $array += ['...'];
    }
}