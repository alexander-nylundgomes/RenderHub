import React, { type SVGProps } from 'react';
// import type { IconType } from '../../types/icon-type';
import type { IconType } from '../icons/types/icon-type';
import * as Icons from '../icons/dist';

export type IconProps = SVGProps<SVGSVGElement> & {
  icon: IconType;
  size?: number;
};

export const Icon: React.FC<IconProps> = ({ icon, size, ...props }) => {
  const Component = React.createElement(Icons[icon], props);

  return (
    <span className="custom-icon block size-fit">
      {Component}
    </span>
  );
};